package com.hotel.common.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordUtil {

    private static final String ALGORITHM = "SHA-256";
    private static final int SALT_LENGTH = 16;

    private PasswordUtil() {}

    public static String encrypt(String password) {
        try {
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[SALT_LENGTH];
            random.nextBytes(salt);

            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            md.update(salt);
            byte[] hash = md.digest(password.getBytes());

            byte[] combined = new byte[salt.length + hash.length];
            System.arraycopy(salt, 0, combined, 0, salt.length);
            System.arraycopy(hash, 0, combined, salt.length, hash.length);

            return Base64.getEncoder().encodeToString(combined);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("加密算法不可用", e);
        }
    }

    public static boolean verify(String password, String encrypted) {
        try {
            byte[] combined = Base64.getDecoder().decode(encrypted);
            byte[] salt = new byte[SALT_LENGTH];
            byte[] hash = new byte[combined.length - SALT_LENGTH];
            System.arraycopy(combined, 0, salt, 0, SALT_LENGTH);
            System.arraycopy(combined, SALT_LENGTH, hash, 0, hash.length);

            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            md.update(salt);
            byte[] newHash = md.digest(password.getBytes());

            return MessageDigest.isEqual(hash, newHash);
        } catch (Exception e) {
            return false;
        }
    }
}