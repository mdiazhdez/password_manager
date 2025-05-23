package com.morsaprogramando.secret_manager.controller;

import com.morsaprogramando.secret_manager.models.KeystoreData;
import com.morsaprogramando.secret_manager.models.StoredPassword;
import com.morsaprogramando.secret_manager.services.EncryptionService;
import com.morsaprogramando.secret_manager.services.FileManagerService;
import com.morsaprogramando.secret_manager.services.PasswordManagerService;
import com.morsaprogramando.secret_manager.view.*;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public enum Controller {
    INSTANCE;

    public void execute() {
        KeystoreData data = initKeyStore();

        if (data == null) return;

        ServicesAndPasswords servicesAndPasswords = initServicesAndPasswords(data);

        KeystoreMenu keystoreMenu = new KeystoreMenu(
                servicesAndPasswords.getPasswordManagerService(),
                servicesAndPasswords.getPasswords(),
                servicesAndPasswords.getFileManagerService());

        keystoreMenu.render();
    }

    private PasswordManagerService initPasswordService(String masterPassword) {
        EncryptionService encryptionService = EncryptionService.create(masterPassword);
        return new PasswordManagerService(encryptionService);
    }

    private KeystoreData initKeyStore() {
        InitialMenu.Action action = InitialMenu.INSTANCE.getAction();
        
        if (action instanceof InitialMenu.Open) {
            return OpenKeystoreMenu.INSTANCE.getData();
        } else if (action instanceof InitialMenu.Create) {
            return CreateKeystoreMenu.INSTANCE.createData();
        } else if (action instanceof InitialMenu.Quit) {
            return null;
        } else {
            throw new IllegalStateException("Acción no reconocida: " + action);
        }
    }

    private ServicesAndPasswords initServicesAndPasswords(KeystoreData data) {
        PasswordManagerService passwordService = initPasswordService(data.getMasterPassword());
        FileManagerService fileManagerService = new FileManagerService(data.getKeyStoreName());

        List<StoredPassword> passwords = data.isNew() ?
                new ArrayList<>() :
                getPasswordsFromKeystore(passwordService, fileManagerService);

        return new ServicesAndPasswords(passwordService, fileManagerService, passwords);
    }

    private List<StoredPassword> getPasswordsFromKeystore(PasswordManagerService passwordService,
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
}
