package com.morsaprogramando.secret_manager.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.security.SecureRandom;

import static com.morsaprogramando.secret_manager.Utils.generateRandomString;

public class EncryptionServiceTest {

    @Test
    public void testEncryption() throws Exception {
        SecureRandom secureRandom = new SecureRandom();

        byte[] key = new byte[16];
        secureRandom.nextBytes(key);

        EncryptionService encryptionService = EncryptionService.create(EncryptionService.encodeUsingHexFormat(key));

        String message = "the secret message: " + generateRandomString();
        String cipherText = encryptionService.encrypt(message);

        String decrypted = encryptionService.decrypt(cipherText);

        Assertions.assertEquals(message, decrypted);
    }
}
