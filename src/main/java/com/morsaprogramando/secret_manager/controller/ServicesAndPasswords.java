package com.morsaprogramando.secret_manager.controller;

import java.util.List;

import com.morsaprogramando.secret_manager.models.StoredPassword;
import com.morsaprogramando.secret_manager.services.FileManagerService;
import com.morsaprogramando.secret_manager.services.PasswordManagerService;

public class ServicesAndPasswords {
	private final PasswordManagerService passwordManagerService;
	private final FileManagerService fileManagerService;
	private final List<StoredPassword> passwords;

	public ServicesAndPasswords(PasswordManagerService passwordManagerService, FileManagerService fileManagerService,
			List<StoredPassword> passwords) {
		super();
		this.passwordManagerService = passwordManagerService;
		this.fileManagerService = fileManagerService;
		this.passwords = passwords;
	}

	public PasswordManagerService getPasswordManagerService() {
		return passwordManagerService;
	}

	public FileManagerService getFileManagerService() {
		return fileManagerService;
	}

	public List<StoredPassword> getPasswords() {
		return passwords;
	}

}
