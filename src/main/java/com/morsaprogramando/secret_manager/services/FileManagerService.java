package com.morsaprogramando.secret_manager.services;

import lombok.RequiredArgsConstructor;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

@RequiredArgsConstructor
public class FileManagerService {

    private final String title;

    private static final String KEYSTORE_EXTENSION = ".msf";

    public byte[] readFile() {
        try (FileInputStream stream = new FileInputStream(title + KEYSTORE_EXTENSION)) {
            return stream.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    FileWriter writeFile() {
        try {
            return new FileWriter(title);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
