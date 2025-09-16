package org.jbrod.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class PassEncrypt {

    public static String encrytp(String usuario, String password){
        String aEncriptar = usuario + "salttt" + password;
        return Base64.getEncoder().encodeToString(aEncriptar.getBytes(StandardCharsets.UTF_8));
    }

}
