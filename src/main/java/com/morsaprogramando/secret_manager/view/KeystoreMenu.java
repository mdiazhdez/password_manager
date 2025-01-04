package com.morsaprogramando.secret_manager.view;

import com.morsaprogramando.secret_manager.models.StoredPassword;
import com.morsaprogramando.secret_manager.services.FileManagerService;
import com.morsaprogramando.secret_manager.services.PasswordManagerService;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class KeystoreMenu {

    private final PasswordManagerService passwordManagerService;
    private final List<StoredPassword> passwords;
    private final FileManagerService fileManagerService;
}
