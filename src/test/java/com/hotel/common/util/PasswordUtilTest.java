package com.hotel.common.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PasswordUtilTest {

    @Test
    public void testEncryptAndVerify() {
        String password = "admin123";
        String encrypted = PasswordUtil.encrypt(password);
        System.out.println("=== ENCRYPTED PASSWORD: " + encrypted + " ===");
        assertTrue(PasswordUtil.verify(password, encrypted));
        assertTrue(!PasswordUtil.verify("wrong", encrypted));
    }

    @Test
    public void testGeneratePasswordForInit() {
        String password = "admin123";
        String encrypted = PasswordUtil.encrypt(password);
        System.out.println("=== INIT SQL PASSWORD ===");
        System.out.println("'admin', '" + encrypted + "', ...");
        System.out.println("=========================");
        assertTrue(PasswordUtil.verify(password, encrypted));
    }
}