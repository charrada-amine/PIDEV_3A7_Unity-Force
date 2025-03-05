package tn.esprit.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordEncryptor {
    public static String encryptPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(password.getBytes());
            byte[] digest = md.digest();
            StringBuilder hexString = new StringBuilder();
            for (byte b : digest) {
                hexString.append(String.format("%02x", b)); // Convertir en hexadécimal
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}
