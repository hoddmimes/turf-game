package com.hoddmimes.turf.servlets;

import java.io.IOException;

public interface ServletResourcesIf
{
    public String sendToTurfServer( String pJsonRequest) throws IOException;
}
