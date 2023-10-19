package com.hoddmimes.turf.server.configuration;

public abstract class CoreConfiguration
{
    private boolean mEnabled;

    CoreConfiguration( boolean pEnabled ) {
        mEnabled = pEnabled;
    }

    public boolean isEnabled() {
        return mEnabled;
    }

    public void enable( boolean pFlag) {
        mEnabled = pFlag;
    }
}
