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

    public PasswordManagerService(EncryptionService encryptionService) {
        this.encryptionService = encryptionService;
    }

    public byte[] encodePasswords(List<StoredPassword> passwords) throws Exception {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

        DataOutputStream dataStream = prepareDataStreamWithHeader(byteStream);
        byte[] serializedData = serializePasswords(passwords);
        byte[] encryptBytes = encryptionService.encryptBytes(serializedData);

        dataStream.writeInt(encryptBytes.length);
        dataStream.write(encryptBytes);
        dataStream.flush();

        return byteStream.toByteArray();
    }

    public List<StoredPassword> decodePasswords(byte[] fileData) throws Exception {
        byte[] encryptedBytes = getEncryptedBytes(fileData);
        byte[] decryptedData = encryptionService.decryptBytes(encryptedBytes);

        return deserializePasswords(decryptedData);
    }

    private DataOutputStream prepareDataStreamWithHeader(ByteArrayOutputStream stream) throws IOException {
        DataOutputStream dataStream = new DataOutputStream(stream);

        dataStream.writeInt(MAGIC_NUMBER);
        dataStream.writeInt(VERSION);

        return dataStream;
    }

    private byte[] serializePasswords(List<StoredPassword> passwords) throws IOException {

        ByteArrayOutputStream passwordsStream = new ByteArrayOutputStream();
        DataOutputStream passwordsDataStream = new DataOutputStream(passwordsStream);
        for (StoredPassword password : passwords) {
            writeString(passwordsDataStream, password.title());
            writeString(passwordsDataStream, password.username());
            writeString(passwordsDataStream, password.password());
        }
        passwordsDataStream.flush();

        return passwordsStream.toByteArray();
    }

    private void validateStream(DataInputStream dataStream) throws IOException {
        int magicNumber = dataStream.readInt();
        if (magicNumber != MAGIC_NUMBER) {
            throw new IllegalArgumentException("Invalid file format: Magic number mismatch.");
        }

        int version = dataStream.readInt();
        if (version != VERSION) {
            throw new IllegalArgumentException("Unsupported file version: " + version);
        }
    }

    private byte[] getEncryptedBytes(byte[] fileData) throws IOException {
        DataInputStream dataStream = new DataInputStream(new ByteArrayInputStream(fileData));
        validateStream(dataStream);

        int encryptedLength = dataStream.readInt();
        byte[] encryptedBytes = new byte[encryptedLength];
        dataStream.readFully(encryptedBytes);

        return encryptedBytes;
    }

    private List<StoredPassword> deserializePasswords(byte[] decryptedData) throws Exception {
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
