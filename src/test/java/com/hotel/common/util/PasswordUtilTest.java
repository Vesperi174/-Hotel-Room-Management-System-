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
    public void testVerifyDatabaseHash() {
        String dbHash = "kZ1D8lXck7KPsATTIoAfzL2rwEH7/2YODC2nzS61HVi9e+M97X+eNsoOwSxEApoz";
        System.out.println("=== Database hash: " + dbHash + " ===");
        System.out.println("=== Verify 'admin123': " + PasswordUtil.verify("admin123", dbHash) + " ===");
        assertTrue(PasswordUtil.verify("admin123", dbHash));
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