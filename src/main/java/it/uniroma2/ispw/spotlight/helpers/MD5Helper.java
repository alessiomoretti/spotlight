package it.uniroma2.ispw.spotlight.helpers;

import java.io.UnsupportedEncodingException;
import java.security.*;

public class MD5Helper {

    public static String getHashedString(String input) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        byte[] bytesOfMessage = input.getBytes("UTF-8");
        MessageDigest md = MessageDigest.getInstance("MD5");
        return md.digest(bytesOfMessage).toString();
    }
}
