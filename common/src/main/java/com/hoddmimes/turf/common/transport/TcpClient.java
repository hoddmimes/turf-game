package com.hoddmimes.turf.common.transport;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;


public class TcpClient {
    String mHost;                            // Host name where the server runs
    int mPort;                                // Port on which the server will accept connections
    TcpThreadCallbackIf mCallbackIf;        // Callback interface
    TcpThread mTcpThread;

    /**
     * Constructor for instantiate a client connection. Is does not establish the connection.
     * By invoking the method connect the connection is established.
     *
     * @param server to which the connetion will be made
     * @param port   on which the connection will be done
     */
    public TcpClient() {
    }

    /**
     * Method issuing a connection. The server and port used will be the ones
     * specified in the constructor
     *
     * @throws IOException throws a I/O exception in case of failure
     */
    public TcpThread connect(String pHost, int pPort, TcpThreadCallbackIf pCallback) throws IOException {
        synchronized (this) {
            mHost = new String(pHost);
            mPort = pPort;

            Socket tSocket = new Socket();
            InetSocketAddress tAddress = new InetSocketAddress(pHost, pPort);
            tSocket.connect(tAddress, 5000); // 5 seconds timeout
            mTcpThread = new TcpThread(tSocket, pCallback);
            mTcpThread.start();
            return mTcpThread;
        }
    }
}
    

 