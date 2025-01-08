package com.morsaprogramando.secret_manager.services;

import lombok.RequiredArgsConstructor;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

@RequiredArgsConstructor
public class FileManagerService {

    private final String title;

    private static final String KEYSTORE_EXTENSION = ".msf";

    public byte[] readFile() throws IOException {
        try (FileInputStream stream = new FileInputStream(getTitleAsFileName())) {
            return stream.readAllBytes();
        }
    }

    public void write(byte[] encryptedPasswords) {
        try (FileOutputStream outputStream = new FileOutputStream(getTitleAsFileName())) {
            outputStream.write(encryptedPasswords);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getTitleAsFileName() {
        if (title.endsWith(KEYSTORE_EXTENSION)) return title;
        return title + KEYSTORE_EXTENSION;
    }
}
