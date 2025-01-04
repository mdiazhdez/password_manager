package com.morsaprogramando.secret_manager.services;

import com.morsaprogramando.secret_manager.models.StoredPassword;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class PasswordManagerService {

    private static final int MAGIC_NUMBER = 0xD9AABCE3;
    private static final int VERSION = 1;

    private static final Charset CHARSET = StandardCharsets.UTF_8;

    private final EncryptionService encryptionService;

    public PasswordManagerService(String masterPassword) {
        this.encryptionService = EncryptionService.create(masterPassword);
    }

    public byte[] encodePasswords(List<StoredPassword> passwords) throws Exception {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        DataOutputStream dataStream = new DataOutputStream(byteStream);

        // Write header
        dataStream.writeInt(MAGIC_NUMBER);
        dataStream.writeInt(VERSION);

        // Serialize passwords into a compact format
        ByteArrayOutputStream passwordsStream = new ByteArrayOutputStream();
        DataOutputStream passwordsDataStream = new DataOutputStream(passwordsStream);
        for (StoredPassword password : passwords) {
            writeString(passwordsDataStream, password.title());
            writeString(passwordsDataStream, password.username());
            writeString(passwordsDataStream, password.password());
        }
        passwordsDataStream.flush();

        // Encrypt serialized password data
        byte[] serializedData = passwordsStream.toByteArray();
        byte[] encryptBytes = encryptionService.encryptBytes(serializedData);

        // Write encrypted data length and data
        dataStream.writeInt(encryptBytes.length);
        dataStream.write(encryptBytes);

        dataStream.flush();
        return byteStream.toByteArray();
    }

    public List<StoredPassword> decodePasswords(byte[] fileData) throws Exception {
        DataInputStream dataStream = new DataInputStream(new ByteArrayInputStream(fileData));

        // Read and validate header
        int magicNumber = dataStream.readInt();
        if (magicNumber != MAGIC_NUMBER) {
            throw new IllegalArgumentException("Invalid file format: Magic number mismatch.");
        }

        int version = dataStream.readInt();
        if (version != VERSION) {
            throw new IllegalArgumentException("Unsupported file version: " + version);
        }

        // Read encrypted data
        int encryptedLength = dataStream.readInt();
        byte[] encryptedBytes = new byte[encryptedLength];
        dataStream.readFully(encryptedBytes);

        // Decrypt data
        byte[] decryptedData = encryptionService.decryptBytes(encryptedBytes);

        // Deserialize passwords
        DataInputStream passwordsStream = new DataInputStream(new ByteArrayInputStream(decryptedData));
        List<StoredPassword> passwords = new ArrayList<>();
        while (passwordsStream.available() > 0) {
            String title = readString(passwordsStream);
            String username = readString(passwordsStream);
            String password = readString(passwordsStream);
            passwords.add(new StoredPassword(title, username, password));
        }

        return passwords;
    }

    private void writeString(DataOutputStream stream, String value) throws IOException {
        byte[] bytes = value.getBytes(CHARSET);
        stream.writeInt(bytes.length);
        stream.write(bytes);
    }

    private String readString(DataInputStream stream) throws Exception {
        int length = stream.readInt();
        byte[] bytes = new byte[length];
        stream.readFully(bytes);
        return new String(bytes, CHARSET);
    }
}
