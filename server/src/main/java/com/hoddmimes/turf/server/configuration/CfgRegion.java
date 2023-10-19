package com.hoddmimes.turf.server.configuration;


public class CfgRegion
{
    private String  mName;
    private int     mId;


    public CfgRegion( String pName, int pId )  {
        setId(pId);
        setName(pName);
    }



    public boolean isSame( int pId, String pName ) {
        if ((pId == getId()) && (pName.equalsIgnoreCase( getName() ))) {
            return true;
        }
        return false;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public int getId() {
        return mId;
    }

    public void setId(int mId) {
        this.mId = mId;
    }
}