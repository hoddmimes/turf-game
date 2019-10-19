package com.hoddmimes.turf.common.transport;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class TcpClientSync {
    private volatile Socket mSocket = null;
    private String mHost;
    private int mPort;

    private DataOutputStream mOutStream;
    private DataInputStream mInStream;


    public TcpClientSync(String pHost, int pPort) {
        mHost = pHost;
        mPort = pPort;
    }

    private void connect(String pHost, int pPort) throws IOException {
        synchronized (this) {
            mSocket = new Socket();
            InetSocketAddress tAddress = new InetSocketAddress(pHost, pPort);
            mSocket.connect(tAddress, 5000); // 5 seconds timeout
            mInStream = new DataInputStream(mSocket.getInputStream());
            mOutStream = new DataOutputStream(mSocket.getOutputStream());
        }
    }

    synchronized public byte[] transcieve(byte[] pBuffer) throws IOException {
        try {
            if (mSocket == null) {
                connect(mHost, mPort);
            }


            mOutStream.writeInt(pBuffer.length);
            mOutStream.write(pBuffer);
            mOutStream.flush();

            int tMsgSize = mInStream.readInt();
            byte[] tBuffer = new byte[tMsgSize];
            mInStream.readFully(tBuffer);
            return tBuffer;
        } catch (IOException e) {
            if (mSocket != null) {
                close();
            }
            mSocket = null;
            throw e;
        }
    }

    public void close() {
        try {
            mSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
