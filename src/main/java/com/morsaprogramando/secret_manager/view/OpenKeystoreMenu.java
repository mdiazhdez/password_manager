package com.morsaprogramando.secret_manager.view;

import com.morsaprogramando.secret_manager.models.KeystoreData;

import java.io.IOException;

public enum OpenKeystoreMenu {
    INSTANCE;

    public KeystoreData getData() {

        try {
            String title = null;
            String defaultSecretFile = System.getProperty("defaultSecretFile");
            if(defaultSecretFile == null || defaultSecretFile.isEmpty()){
                title = Utils.readLine("Title of the existing keystore: ");
            }else{
                Utils.println("Loading defaultSecretKey "+ defaultSecretFile);
                title = defaultSecretFile;
            }

            String masterPassword = Utils.readPassword("Enter the master password: ");
            return new KeystoreData(title, masterPassword, false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
