package it.uniroma2.ispw.spotlight.helpers;

import javax.xml.bind.DatatypeConverter;
import java.io.UnsupportedEncodingException;
import java.security.*;

public class MD5Helper {

    /**
     * Return the hashcode of the input string
     * @param input String
     * @return String
     */
    public static String getHashedString(String input){
        try {
            byte[] bytesOfMessage = input.getBytes("UTF-8");
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(bytesOfMessage);
            return DatatypeConverter.printHexBinary(md.digest()).toLowerCase();
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }

    }
}
