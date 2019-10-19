package com.hoddmimes.turf.server.configuration;

import org.w3c.dom.Element;

public class ZoneNotifyConfiguration
{


        String mMailUser = null;
        String mMailPassw = null;
        String mMailHost = null;

        PasswordRules mPasswRules;


        public void parse( Element pRootElement) {
            Element tZNElement = XmlAux.getElement( pRootElement, "ZoneNotify");

            Element tMailElement = XmlAux.getElement( tZNElement, "Mailer");
            mMailUser = XmlAux.getStringAttribute(tMailElement,"user", null);
            mMailPassw = XmlAux.getStringAttribute(tMailElement,"password", null);
            mMailHost =  XmlAux.getStringAttribute(tMailElement,"host", null);




            Element tPasswElement = XmlAux.getElement( tZNElement, "PasswordRules");
            mPasswRules = new PasswordRules();
            mPasswRules.setDigits( XmlAux.getBooleanAttribute( tPasswElement, "digits", false));
            mPasswRules.setLowerAndUpperCase( XmlAux.getBooleanAttribute( tPasswElement, "lowerAndUpercase", false));
            mPasswRules.setMinLength( XmlAux.getIntAttribute(tPasswElement,"minLength", 0));
        }





        public String getMailerHost() {
        return this.mMailHost;
    }

        public String getMailerUser() { return this.mMailUser; }

        public String getMailerPassword() {
        return this.mMailPassw;
    }

        public PasswordRules getPasswordRules() { return mPasswRules; }

}
