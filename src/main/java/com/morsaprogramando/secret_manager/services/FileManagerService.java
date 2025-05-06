package com.morsaprogramando.secret_manager.services;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileManagerService {

    private final String title;

    public FileManagerService(String title) {
		this.title = title;
	}

	private static final String KEYSTORE_EXTENSION = ".wsf";

    public byte[] readFile() throws IOException {
    	try (FileInputStream stream = new FileInputStream(getTitleAsFileName());
    	         ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {

    	        byte[] data = new byte[1024];
    	        int bytesRead;
    	        while ((bytesRead = stream.read(data, 0, data.length)) != -1) {
    	            buffer.write(data, 0, bytesRead);
    	        }
    	        return buffer.toByteArray();
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
