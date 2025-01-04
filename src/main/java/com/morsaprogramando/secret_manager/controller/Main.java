package com.morsaprogramando.secret_manager.controller;

import com.morsaprogramando.secret_manager.models.KeystoreData;
import com.morsaprogramando.secret_manager.models.StoredPassword;
import com.morsaprogramando.secret_manager.services.EncryptionService;
import com.morsaprogramando.secret_manager.services.PasswordManagerService;
import com.morsaprogramando.secret_manager.view.InitialMenu;
import com.morsaprogramando.secret_manager.view.NewKeystoreMenu;

import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {
        InitialMenu.Action action = InitialMenu.INSTANCE.getAction();

        switch (action) {
            case InitialMenu.Open __ -> { openKeyStore(); }
            case InitialMenu.Create __ -> { createNewKeyStore(); }
            case InitialMenu.Quit __ -> { return; }
        }
    }

    private static void createNewKeyStore() {
        KeystoreData data = NewKeystoreMenu.INSTANCE.createData();
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
