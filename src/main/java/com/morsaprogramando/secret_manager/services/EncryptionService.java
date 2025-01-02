package com.morsaprogramando.secret_manager.services;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.util.HexFormat;

public class EncryptionService {

    private final SecureRandom secureRandom = new SecureRandom();
    private final static int GCM_IV_LENGTH = 12;
    private final SecretKey secretKey;
    private final byte[] associatedData;

    private EncryptionService(byte[] keyHex) {
        secretKey = new SecretKeySpec(keyHex, "AES");
        associatedData = "ProtocolVersion1".getBytes(StandardCharsets.UTF_8);
    }

    private EncryptionService(String keyHex) {
        secretKey = new SecretKeySpec(decodeUsingHexFormat(keyHex), "AES");
        associatedData = "ProtocolVersion1".getBytes(StandardCharsets.UTF_8);
    }

    public static EncryptionService create(String keyHex) {
        return new EncryptionService(keyHex);
    }

    public static EncryptionService createFromBytes(byte[] key) {
        return new EncryptionService(key);
    }

    public byte[] encryptBytes(byte[] textInBytes) throws Exception {

        byte[] iv = new byte[GCM_IV_LENGTH];
        secureRandom.nextBytes(iv);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec parameterSpec = new GCMParameterSpec(128, iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);

        if (associatedData != null) {
            cipher.updateAAD(associatedData);
        }

        byte[] cipherText = cipher.doFinal(textInBytes);

        ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + cipherText.length);
        byteBuffer.put(iv);
        byteBuffer.put(cipherText);
        return byteBuffer.array();
    }

    public String encrypt(String plaintext) throws Exception {
        return encodeUsingHexFormat(encryptBytes(plaintext.getBytes(StandardCharsets.UTF_8)));
    }

    public String decrypt(String cipherMessage) throws Exception {
        return new String(decryptHex(decodeUsingHexFormat(cipherMessage)), StandardCharsets.UTF_8);
    }

    public byte[] decryptHex(byte[] cipherMessage) throws Exception {
        final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

        AlgorithmParameterSpec gcmIv = new GCMParameterSpec(128, cipherMessage, 0, GCM_IV_LENGTH);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmIv);

        if (associatedData != null) {
            cipher.updateAAD(associatedData);
        }
        return cipher.doFinal(cipherMessage, GCM_IV_LENGTH, cipherMessage.length - GCM_IV_LENGTH);
    }

    protected static String encodeUsingHexFormat(byte[] bytes) {
        HexFormat hexFormat = HexFormat.of();
        return hexFormat.formatHex(bytes);
    }

    private static byte[] decodeUsingHexFormat(String hexString) {
        HexFormat hexFormat = HexFormat.of();
        return hexFormat.parseHex(hexString);
    }
}
