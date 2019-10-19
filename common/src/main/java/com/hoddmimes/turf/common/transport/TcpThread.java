package com.hoddmimes.turf.common.transport;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;


public class TcpThread extends Thread {
    private static final SimpleDateFormat cSDF = new SimpleDateFormat("HH:mm:ss.SSS");
    private static int mClientIndex = 0;
    private Socket mSocket;
    private TcpThreadCallbackIf mCallbackIf;
    private volatile boolean mClosed = true;
    private int mIndex;
    private String mConnectTime;
    private DataOutputStream mOutStream;

    public TcpThread(Socket pSocket, TcpThreadCallbackIf pCallbackIf) {
        mSocket = pSocket;
        mClosed = false;
        mIndex = ++mClientIndex;
        mConnectTime = cSDF.format(System.currentTimeMillis());

        mCallbackIf = pCallbackIf;
        try {
            mOutStream = new DataOutputStream(mSocket.getOutputStream());
            mSocket.setKeepAlive(true);
            mSocket.setTcpNoDelay(true);
            //start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getRemoteAddress() {
        return mSocket.getInetAddress().getHostAddress();
    }

    public String toString() {
        return "[TcpThread: " + mIndex + " addr: " + getRemoteAddress() + " connTime: " + mConnectTime + "]";
    }

    public void setCallback(TcpThreadCallbackIf pCallbackIf) {
        mCallbackIf = pCallbackIf;
    }

    public void close() {
        synchronized (this) {
            mClosed = true;
            this.notify();
            try {
                mSocket.close();
            } catch (IOException e) {
            }
        }
    }

    public void send(byte[] pBuffer) throws IOException {
        synchronized (this) {
            mOutStream.writeInt(pBuffer.length);
            mOutStream.write(pBuffer);
            mOutStream.flush();
        }
    }

    public void run() {
        byte[] tBuffer;
        int tSize;
        DataInputStream tStream = null;

        try {
            tStream = new DataInputStream(mSocket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            if (!mClosed) {
                mCallbackIf.tcpErrorEvent(this, e);
                return;
            } else {
                return;
            }

        }

        while (!mClosed) {
            try {
                tSize = tStream.readInt();
                tBuffer = new byte[tSize];
                tStream.readFully(tBuffer);
            } catch (IOException e) {
                if (!mClosed) {
                    mCallbackIf.tcpErrorEvent(this, e);
                    return;
                } else {
                    return;
                }
            }
            try {
                mCallbackIf.tcpMessageRead(this, tBuffer);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }
}
