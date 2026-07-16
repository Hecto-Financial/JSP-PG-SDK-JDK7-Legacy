package com.settle.pg;

import java.security.MessageDigest;
import java.util.Arrays;

import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.paddings.PKCS7Padding;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.encoders.Base64;

/**
 * JDK 7 legacy AES utility.
 *
 * <p>Bouncy Castle lightweight API를 사용하므로 가맹점 JRE의
 * AES 최대 허용 키 길이(JCE policy)에 영향을 받지 않습니다.</p>
 */
public final class EncryptUtil {
    private static final String UTF_8 = "UTF-8";
    private static final int AES_256_KEY_BYTES = 32;

    private EncryptUtil() {
    }

    public static String encryptParam(String aesKey, String plainText) throws Exception {
        if (plainText == null || plainText.length() == 0) {
            return "";
        }
        return encodeBase64(aes256EncryptEcb(aesKey, plainText));
    }

    public static String decryptParam(String aesKey, String cipherText) throws Exception {
        if (cipherText == null || cipherText.length() == 0) {
            return "";
        }
        return new String(aes256DecryptEcb(aesKey, decodeBase64(cipherText)), UTF_8);
    }

    public static String digestSHA256(String plain) throws Exception {
        if (plain == null) {
            return null;
        }

        byte[] digest = MessageDigest.getInstance("SHA-256").digest(plain.getBytes(UTF_8));
        StringBuilder hex = new StringBuilder(digest.length * 2);
        for (int i = 0; i < digest.length; i++) {
            hex.append(Integer.toString((digest[i] & 0xff) + 0x100, 16).substring(1));
        }
        return hex.toString();
    }

    public static byte[] aes256EncryptEcb(String key, String plainText) throws Exception {
        requireNonNull(plainText, "plainText");
        return process(true, normalizeKey(key), plainText.getBytes(UTF_8));
    }

    public static byte[] aes256DecryptEcb(String key, byte[] encrypted) throws Exception {
        requireNonNull(encrypted, "encrypted");
        return process(false, normalizeKey(key), encrypted);
    }

    public static String encodeBase64(byte[] value) throws Exception {
        if (value == null) {
            return null;
        }
        return new String(Base64.encode(value), "US-ASCII");
    }

    public static byte[] decodeBase64(String value) throws Exception {
        if (value == null) {
            return null;
        }
        return Base64.decode(value);
    }

    public static byte[] decodeBase64(byte[] value) throws Exception {
        if (value == null) {
            return null;
        }
        return Base64.decode(value);
    }

    private static byte[] normalizeKey(String key) throws Exception {
        requireNonNull(key, "key");
        return Arrays.copyOf(key.getBytes(UTF_8), AES_256_KEY_BYTES);
    }

    private static byte[] process(boolean encrypt, byte[] key, byte[] input)
            throws InvalidCipherTextException {
        BufferedBlockCipher cipher = new PaddedBufferedBlockCipher(
                AESEngine.newInstance(), new PKCS7Padding());
        cipher.init(encrypt, new KeyParameter(key));

        byte[] output = new byte[cipher.getOutputSize(input.length)];
        int outputLength = cipher.processBytes(input, 0, input.length, output, 0);
        outputLength += cipher.doFinal(output, outputLength);
        return Arrays.copyOf(output, outputLength);
    }

    private static void requireNonNull(Object value, String name) {
        if (value == null) {
            throw new IllegalArgumentException(name + " must not be null");
        }
    }
}
