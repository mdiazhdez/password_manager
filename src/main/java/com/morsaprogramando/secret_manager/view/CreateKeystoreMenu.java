package com.morsaprogramando.secret_manager.view;

import com.morsaprogramando.secret_manager.models.KeystoreData;

import java.io.IOException;
import java.util.Objects;

public enum CreateKeystoreMenu {
    INSTANCE;

    public KeystoreData createData() {
        try {
            String title = Utils.readLine("Title of the new keystore: ");
            String password = Utils.readLine("New master password: ");
            String verifyPassword = "";

            while (!Objects.equals(verifyPassword, password)
                    || password.isEmpty()) {

                if (!verifyPassword.isEmpty()) {
                    Utils.println("Password doesn't mach, try again.");

                    password = Utils.readLine("New master password: ");
                }

                while (password.length() <= 6) {
                    Utils.println("Password must be longer than 6 characters.");
                    password = Utils.readLine("New master password: ");
                }

                verifyPassword = Utils.readLine("Repeat master password: ");
            }

            return new KeystoreData(title, password, true);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
