package com.hoddmimes.turf.server.configuration;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PasswordRules
{
    private int mMinLength;
    private boolean mLowerAndUpperCase;
    private boolean mDigits;

    public void setMinLength( int pMinLength ) {
        mMinLength = pMinLength;
    }

    public void setLowerAndUpperCase( boolean pFlag ) {
        mLowerAndUpperCase = pFlag;
    }

    public void setDigits( boolean pFlag ) {
        mDigits = pFlag;
    }

    public void validatePassword( String pPassword ) throws Exception
    {
        if (pPassword.length() < mMinLength ) {
            throw  new Exception("password min length is set to " + String.valueOf( mMinLength ));
        }

        if (mLowerAndUpperCase) {
            if (pPassword.compareTo(pPassword.toLowerCase()) == 0) {
                throw new Exception("password must contain at least one uppercase letter");
            }
            if (pPassword.compareTo(pPassword.toUpperCase()) == 0) {
                throw new Exception("password must contain at least one lowercase letter");
            }
        }

        if (mDigits) {
            Pattern p = Pattern.compile( "[0-9]" );
            Matcher m = p.matcher( pPassword );
            if (!m.find()) {
                throw new Exception("password must contain at least one digit character");
            }
        }
    }

    public static String hashPassword( String pPassword ) {
        String tPassword = pPassword + "Call me Ishmael. Some years ago—never mind...";

        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            byte[] passBytes = tPassword.getBytes();
            byte[] passHash = sha256.digest(passBytes);
            return new String(Base64.getEncoder().encode(passHash));
        }
        catch( NoSuchAlgorithmException e) {
            e.printStackTrace();
            System.exit(0);
        }
        return null;
    }
}
