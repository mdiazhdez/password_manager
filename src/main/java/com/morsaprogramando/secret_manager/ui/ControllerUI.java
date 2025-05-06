package com.morsaprogramando.secret_manager.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import com.morsaprogramando.secret_manager.controller.ServicesAndPasswords;
import com.morsaprogramando.secret_manager.models.KeystoreData;
import com.morsaprogramando.secret_manager.models.StoredPassword;
import com.morsaprogramando.secret_manager.services.EncryptionService;
import com.morsaprogramando.secret_manager.services.FileManagerService;
import com.morsaprogramando.secret_manager.services.PasswordManagerService;

public enum ControllerUI {
    INSTANCE;
	
	public ServicesAndPasswords open(File file, String password){
		 KeystoreData data = new KeystoreData(file.getAbsolutePath(), password, false);
         return initServicesAndPasswords(data);
	}
	
	public ServicesAndPasswords create(File file, String password){
		KeystoreData data = new KeystoreData(file.getAbsolutePath(), password, true);
		return initServicesAndPasswords(data);
	}

    private PasswordManagerService initPasswordService(String masterPassword) {
        EncryptionService encryptionService = EncryptionService.create(masterPassword);
        return new PasswordManagerService(encryptionService);
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
        	throw new RuntimeException("The keystore was not found. Try again writing the correct keystore name (without extension).");
        } catch (Exception e) {
        	throw new RuntimeException("The password is incorrect or the file is corrupted.");
        }

    }
}
