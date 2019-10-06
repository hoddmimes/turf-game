package com.hoddmimes.turf.common.transport;
import java.io.IOException;


public interface TcpThreadCallbackIf 
{
	public void tcpMessageRead( TcpThread pThread, byte[] pBuffer );
	public void tcpErrorEvent( TcpThread pThread, IOException pException );
}
