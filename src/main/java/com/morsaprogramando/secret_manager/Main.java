package com.morsaprogramando.secret_manager;

import com.morsaprogramando.secret_manager.models.StoredPassword;
import com.morsaprogramando.secret_manager.services.PasswordManagerService;

import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {
        String masterPassword = "unpasswordmuyseguro123!";

        PasswordManagerService manager = new PasswordManagerService(masterPassword);

        // Example: Encode passwords
        List<StoredPassword> passwords = List.of(
                new StoredPassword("user1", "password1"),
                new StoredPassword("user2", "password2")
        );
        byte[] encodedData = manager.encodePasswords(passwords);

        // Example: Decode passwords
        List<StoredPassword> decodedPasswords = manager.decodePasswords(encodedData);
        decodedPasswords.forEach(pw ->
                System.out.println("Username: " + pw.username() + ", Password: " + pw.password())
        );
    }
}
