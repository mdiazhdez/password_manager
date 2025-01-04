package com.morsaprogramando.secret_manager.view;

import com.morsaprogramando.secret_manager.models.KeystoreData;

import java.io.IOException;
import java.util.Objects;

public enum NewKeystoreMenu {
    INSTANCE;

    public KeystoreData createData() {
        try {
            String title = Utils.readLine("Title of the new keystore: ");
            String password = Utils.readLine("New password: ");
            String verifyPassword = "";

            while (!Objects.equals(verifyPassword, password)
                    || password.isEmpty()) {

                if (!verifyPassword.isEmpty() && !verifyPassword.isBlank()) {
                    Utils.println("Password doesn't mach, try again.");

                    password = Utils.readLine("New password: ");
                }

                while (password.length() <= 6) {
                    Utils.println("Password must be longer than 6 characters.");
                    password = Utils.readLine("New password: ");
                }

                verifyPassword = Utils.readLine("Repeat password: ");
            }

            return new KeystoreData(title, password);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
