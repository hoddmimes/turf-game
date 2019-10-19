package com.hoddmimes.turf.common.transport;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;


public class TcpServer extends Thread {
    ServerSocket mServerSocket;
    Map<TcpThread, TcpThread> mClients;
    TcpServerCallbackIf mCallback;


    public TcpServer(TcpServerCallbackIf pCallback) {
        mClients = new HashMap<TcpThread, TcpThread>();
        mCallback = pCallback;
        ;
    }

    public void declareServer(int pAcceptPort, String pInterface) throws IOException {
        try {
            InetAddress tBindAddr = (pInterface == null) ? InetAddress.getByName("0.0.0.0") : InetAddress.getByName(pInterface);
            mServerSocket = new ServerSocket(pAcceptPort, 10, tBindAddr);
            this.start();
        } catch (IOException e) {
            throw e;
        }
    }

    public void run() {
        Socket tSocket = null;

        while (true) {
            try {
                tSocket = mServerSocket.accept();
                try {
                    TcpThread tThread = new TcpThread(tSocket, null);
                    mCallback.tcpInboundConnection(tThread);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
	
	
