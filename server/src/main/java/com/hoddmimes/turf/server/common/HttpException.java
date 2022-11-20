package com.hoddmimes.turf.server.common;

public class HttpException extends Exception {
    private int mHttpResponseCode;

    HttpException( Exception e, int pResponseCode ) {
        super( e );
        mHttpResponseCode = pResponseCode;
    }

    public int getHttpResponseCode() {
        return mHttpResponseCode;
    }
}
