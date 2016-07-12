package com.ylitormatech.sensorserver.utils;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by marco on 6.6.2016.
 */
public class ApiKeyGenerator {

    public String createNewApiKey(String data) {
        return generateApiKey(data);
    }

    private String generateApiKey(String txt) {
        String result = null;
        String data = txt + new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes("UTF-8"));
            return bytesToHex(hash);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private String bytesToHex(byte[] hash) {
        return DatatypeConverter.printHexBinary(hash);
    }
}
