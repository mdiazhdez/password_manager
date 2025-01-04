package com.morsaprogramando.secret_manager.view;

import com.morsaprogramando.secret_manager.models.KeystoreData;

import java.io.IOException;

public enum OpenKeystoreMenu {
    INSTANCE;

    public KeystoreData getData() {

        try {
            String title = Utils.readLine("Title of the existing keystore: ");
            String masterPassword = Utils.readLine("Enter the master password: ");

            return new KeystoreData(title, masterPassword, false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
