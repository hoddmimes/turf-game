package com.hoddmimes.turf.server.common;

import com.sun.mail.util.MailSSLSocketFactory;

import java.security.GeneralSecurityException;
import java.util.Properties;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;




public class Mailer
{
    ExecutorService 	mExecutor;
    MailConfiguration 	mMailConf;


    public Mailer( String pHost, int pPort, String pUsername, String pPassword, String pContentType, boolean pDebug )
    {
        mExecutor = Executors.newFixedThreadPool(3);
        mMailConf = new MailConfiguration( pHost, pPort, pUsername, pPassword, pContentType, pDebug);

    }


    public void sendMessage(boolean pSynchronized, String pFromAddress, String pTOAddress, String pCCAddress, String pSubject, String pMessage )
    {
        MailTask tTask = new MailTask(mMailConf, pFromAddress, pTOAddress, pCCAddress, pSubject, pMessage);
        mExecutor.execute(tTask);
        if (pSynchronized) {
            synchronized (tTask) {
                try {
                    tTask.wait();
                } catch (InterruptedException ie) {
                }
            }
        }
    }


    public static void main(String[] args) {
        Mailer m = new  Mailer("smtp.hoddmimes.com",
                                587,
                            "yyyyyyyyyyy",
                            "xxxxxxxxxxx",
                            "text/html", true);
        m.sendMessage( true,
                "turf-mailer",
                "par.bertilsson@hoddmimes.com",
                null,
                "Mailer Test HTML",
                "<!DOCTYPE html><html><body><h1>TestService World!</h1></body></html>");
        System.out.println("thats all");
    }

    static class SMTPAuthenticator extends Authenticator {

        private final String username;
        private final String password;

        public SMTPAuthenticator(String pEmailUser, String pEmailUserPassword) {
            this.username = pEmailUser;
            this.password = pEmailUserPassword;
        }

        @Override
        public PasswordAuthentication getPasswordAuthentication() {

            return new PasswordAuthentication(username, password);
        }
    }

    static class MailTask implements Runnable
    {
        private MailSSLSocketFactory mMSF = null;
        private String mToAddress;
        private String mCcAddress;
        private String mFromAddress;
        private String mSubject;
        private String mMessageText;
        private MailConfiguration mEmailCfg;

        public MailTask( MailConfiguration pConfiguration, String pFromAddress, String pTOAddress, String pCCAddress, String pSubject, String pMessage ) {
            mToAddress = pTOAddress;
            mFromAddress = pFromAddress;
            mCcAddress = pCCAddress;
            mSubject = pSubject;
            mMessageText = pMessage;
            mEmailCfg = pConfiguration;
        }

        @SuppressWarnings("static-access")
        @Override
        public void run() {
            Properties tProps = System.getProperties();
            tProps.put("mail.smtp.host", mEmailCfg.mHost);
            tProps.put("mail.smtp.ssl.checkserveridentity", "false");
            tProps.put("mail.smtp.auth", "true");


            if (mEmailCfg.mPort == 465) // SSL is requested
            {
                tProps.put("mail.smtp.starttls.enable", "false");
                tProps.put("mail.transport.protocol", "smtps");
                tProps.put("mail.smtp.port", String.valueOf(465));
                tProps.put("mail.smtp.socketFactory.port", String.valueOf(465));
                tProps.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            } else { // Should be port 587 starttls
                try {
                    mMSF = new MailSSLSocketFactory();
                    mMSF.setTrustAllHosts(true);
                }
                catch( GeneralSecurityException e) {
                    e.printStackTrace();
                }
                tProps.put("mail.smtp.port", String.valueOf(587));
                tProps.put("mail.smtp.ssl.socketFactory", mMSF);
                tProps.put("mail.smtp.starttls.enable", "true");
            }

            SMTPAuthenticator tAuth = new SMTPAuthenticator(mEmailCfg.mUsername,mEmailCfg.mPassword);

            Session tSession = Session.getInstance(tProps, tAuth);
            tSession.setDebug(mEmailCfg.mDebug);

            // Construct the message and send it.
            try {
                Message tMessage = new MimeMessage(tSession);
                tMessage.setFrom(new InternetAddress(mFromAddress));
                tMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(mToAddress, false));
                if (mCcAddress != null) {
                    tMessage.setRecipients(Message.RecipientType.CC, InternetAddress.parse(mCcAddress, false));
                }
                tMessage.setContent(mMessageText, mEmailCfg.mContentType + "; charset=utf-8");
                tMessage.setSubject(mSubject);

                // send the thing off
                Transport tTransport = (mEmailCfg.mPort == 465) ? tSession.getTransport("smtps") : tSession.getTransport("smtp");
                tTransport.connect (mEmailCfg.mHost, mEmailCfg.mPort, mEmailCfg.mUsername, mEmailCfg.mPassword);
                tTransport.sendMessage(tMessage, tMessage.getAllRecipients());

            }
            catch( Exception e ) {
                e.printStackTrace();
            }

            synchronized( this ) {
                this.notifyAll();
            }
        }

    }

    static class MailConfiguration {
        String mHost;
        int    mPort;
        String mUsername;
        String mPassword;
        String mContentType;
        boolean mDebug;

        MailConfiguration( String pHost, int pPort, String pUsername, String pPassword, String pContentType, boolean pDebug ) {
            mHost = pHost;
            mPort = pPort;
            mUsername = pUsername;
            mPassword = pPassword;
            mContentType = pContentType;
            mDebug = pDebug;
        }
    }

}
