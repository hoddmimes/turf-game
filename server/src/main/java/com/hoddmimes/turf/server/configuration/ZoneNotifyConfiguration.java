package com.hoddmimes.turf.server.configuration;

import org.w3c.dom.Element;

public class ZoneNotifyConfiguration
{
        String mDbHost = null;
        int    mDbPort = 0;
        String mDbName = null;

        String mMailUser = null;
        String mMailPassw = null;
        String mMailHost = null;


        public void parse( Element pRootElement) {
            Element tZNElement = XmlAux.getElement( pRootElement, "ZoneNotify");

            Element tMailElement = XmlAux.getElement( tZNElement, "Mailer");
            mMailHost = XmlAux.getStringAttribute(tMailElement,"user", null);
            mMailPassw = XmlAux.getStringAttribute(tMailElement,"password", null);
            mMailHost =  XmlAux.getStringAttribute(tMailElement,"host", null);


            Element tDBElement = XmlAux.getElement( tZNElement, "Database");
            mDbHost = XmlAux.getStringAttribute(tMailElement,"host", null);
            mDbPort = XmlAux.getIntAttribute(tMailElement,"port", 0);
            mDbName =  XmlAux.getStringAttribute(tMailElement,"name", null);
        }

        public String getDbHost() {
            return this.mDbHost;
        }

        public String getDbName() {
            return this.mDbName;
        }

        public int getDbPort() {
            return this.mDbPort;
        }


    public String getMailerHost() {
        return this.mMailHost;
    }

    public String getMailerUser() {
        return this.mMailUser;
    }

    public String getMailerPassword() {
        return this.mMailPassw;
    }

}
