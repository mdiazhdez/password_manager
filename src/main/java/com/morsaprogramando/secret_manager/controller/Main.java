package com.morsaprogramando.secret_manager.controller;

import com.morsaprogramando.secret_manager.models.StoredPassword;
import com.morsaprogramando.secret_manager.services.EncryptionService;
import com.morsaprogramando.secret_manager.services.PasswordManagerService;
import com.morsaprogramando.secret_manager.view.InitialMenu;

import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {
        InitialMenu.Action action = InitialMenu.INSTANCE.getAction();

        switch (action) {
            case CREATE -> { createNewKeyStore(); }
            case OPEN -> { openKeyStore(); }
            case QUIT -> { return; }
        }
    }

    private static void createNewKeyStore() {

    }

    private static void openKeyStore() {}

    private static void test() throws Exception {
        String masterPassword = "unpasswordmuyseguro123!";

        // Dependency injection
        EncryptionService encryptionService = EncryptionService.create(masterPassword);
        PasswordManagerService manager = new PasswordManagerService(encryptionService);
        // --------------------

        List<StoredPassword> passwords = List.of(
                new StoredPassword("mail", "user1", "password1"),
                new StoredPassword("bank", "user2", "password2")
        );
        byte[] encodedData = manager.encodePasswords(passwords);

        List<StoredPassword> decodedPasswords = manager.decodePasswords(encodedData);
        decodedPasswords.forEach(pw ->
                System.out.println("Title: " + pw.title() + ", Username: " + pw.username() + ", Password: " + pw.password())
        );
    }
}
