package com.morsaprogramando.secret_manager.controller;

import com.morsaprogramando.secret_manager.models.KeystoreData;
import com.morsaprogramando.secret_manager.models.StoredPassword;
import com.morsaprogramando.secret_manager.services.EncryptionService;
import com.morsaprogramando.secret_manager.services.FileManagerService;
import com.morsaprogramando.secret_manager.services.PasswordManagerService;
import com.morsaprogramando.secret_manager.view.*;

import java.io.FileNotFoundException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        InitialMenu.Action action = InitialMenu.INSTANCE.getAction();

        KeystoreData data = switch (action) {
            case InitialMenu.Open __ -> OpenKeystoreMenu.INSTANCE.getData();
            case InitialMenu.Create __ -> CreateKeystoreMenu.INSTANCE.createData();
            case InitialMenu.Quit __ -> null;
        };

        if (data == null) System.exit(0);

        PasswordManagerService passwordService = initPasswordService(data.masterPassword());
        FileManagerService fileManagerService = new FileManagerService(data.keyStoreName());

        List<StoredPassword> passwords = data.isNew() ?
                new ArrayList<>() :
                getPasswordsFromKeystore(passwordService, fileManagerService);

        KeystoreMenu keystoreMenu = new KeystoreMenu(passwordService, passwords, fileManagerService);

        keystoreMenu.render();
    }

    private static PasswordManagerService initPasswordService(String masterPassword) {
        EncryptionService encryptionService = EncryptionService.create(masterPassword);
        return new PasswordManagerService(encryptionService);
    }

    private static List<StoredPassword> getPasswordsFromKeystore(PasswordManagerService passwordService,
                                                                 FileManagerService fileManagerService) {
        try {
            return passwordService.decodePasswords(fileManagerService.readFile());

        } catch (FileNotFoundException e) {
            Utils.println("The keystore was not found. Try again writing the correct keystore name (without extension).");
            System.exit(1);
        } catch (Exception e) {
            Utils.println("The password is incorrect or the file is corrupted.");
            System.exit(1);
        }

        return null;
    }

    private static void test() throws Exception {
        String masterPassword = "unpasswordmuyseguro123!";

        // Dependency injection
        EncryptionService encryptionService = EncryptionService.create(masterPassword);
        PasswordManagerService manager = new PasswordManagerService(encryptionService);
        // --------------------

        List<StoredPassword> passwords = List.of(
                new StoredPassword("mail", "user1", "password1", Instant.now()),
                new StoredPassword("bank", "user2", "password2", Instant.now())
        );
        byte[] encodedData = manager.encodePasswords(passwords);

        List<StoredPassword> decodedPasswords = manager.decodePasswords(encodedData);
        decodedPasswords.forEach(pw ->
                System.out.println("Title: " + pw.title() + ", Username: " + pw.username() + ", Password: " + pw.password())
        );
    }
}
