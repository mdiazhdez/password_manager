package com.morsaprogramando.secret_manager.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static com.morsaprogramando.secret_manager.Utils.generateRandomString;

public class EncryptionServiceTest {

    @Test
    public void testEncryption() throws Exception {

        EncryptionService encryptionService = EncryptionService.create("Strong password");

        String message = "the secret message: " + generateRandomString();

        byte[] decrypted = encryptionService.decryptBytes(encryptionService.encryptBytes(
                message.getBytes(StandardCharsets.UTF_8)));

        Assertions.assertEquals(message, new String(decrypted, StandardCharsets.UTF_8));
    }
}
