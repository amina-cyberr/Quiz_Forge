package util;

import java.security.MessageDigest;
import java.security.SecureRandom;

public class PasswordUtil {
    public static String generateSalt() {
        byte[] b = new byte[12];
        new SecureRandom().nextBytes(b);
        StringBuilder sb = new StringBuilder();
        for (byte x : b) sb.append(String.format("%02x", x));
        return sb.toString();
    }

    public static String hash(String salt, String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] out = md.digest((salt + password).getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte x : out) sb.append(String.format("%02x", x));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
