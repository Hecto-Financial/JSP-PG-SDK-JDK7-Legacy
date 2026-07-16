package com.settle.pg;

import java.security.InvalidKeyException;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public final class EncryptUtilCompatibilityTest {
    private static final String KEY = "pgSettle30y739r82jtd709yOfZ2yK5K";
    private static final String PLAIN_TEXT = "헥토파이낸셜 AES-256 호환성 테스트";
    private static final String EXPECTED_BASE64 =
            "z1NXwx+Muz+LTucnSi2lxjwgD3psQwUZ5/dz4mxeDYVqU0cW9bMLaSw9QELwXIEQ";

    public static void main(String[] args) throws Exception {
        String encrypted = EncryptUtil.encryptParam(KEY, PLAIN_TEXT);
        assertEquals(EXPECTED_BASE64, encrypted, "known AES-256 test vector");
        assertEquals(PLAIN_TEXT, EncryptUtil.decryptParam(KEY, encrypted), "round trip");
        assertEquals(
                "9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08",
                EncryptUtil.digestSHA256("test"),
                "SHA-256");

        int maxAllowed = Cipher.getMaxAllowedKeyLength("AES");
        if (maxAllowed >= 256) {
            assertStandardJceCompatibility(encrypted);
        } else {
            assertStandardJceRejectsAes256();
        }

        System.out.println("java.version=" + System.getProperty("java.version"));
        System.out.println("AES max allowed key length=" + maxAllowed);
        System.out.println("legacy ciphertext=" + encrypted);
        System.out.println("ALL TESTS PASSED");
    }

    private static void assertStandardJceCompatibility(String encrypted) throws Exception {
        byte[] normalizedKey = Arrays.copyOf(KEY.getBytes("UTF-8"), 32);
        Cipher jceCipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        jceCipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(normalizedKey, "AES"));
        String jceEncrypted = EncryptUtil.encodeBase64(
                jceCipher.doFinal(PLAIN_TEXT.getBytes("UTF-8")));
        assertEquals(jceEncrypted, encrypted, "standard JCE compatibility");
    }

    private static void assertStandardJceRejectsAes256() throws Exception {
        byte[] normalizedKey = Arrays.copyOf(KEY.getBytes("UTF-8"), 32);
        Cipher jceCipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        try {
            jceCipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(normalizedKey, "AES"));
            throw new AssertionError("standard JCE unexpectedly accepted AES-256");
        } catch (InvalidKeyException expected) {
            System.out.println("standard JCE AES-256 rejection confirmed: " + expected.getMessage());
        }
    }

    private static void assertEquals(String expected, String actual, String label) {
        if (!expected.equals(actual)) {
            throw new AssertionError(label + ": expected=" + expected + ", actual=" + actual);
        }
    }
}
