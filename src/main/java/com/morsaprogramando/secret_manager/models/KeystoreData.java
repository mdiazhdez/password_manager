package com.morsaprogramando.secret_manager.models;

public class KeystoreData {
	private final String keyStoreName;
	private final String masterPassword;
	private final boolean isNew;

	public KeystoreData(String keyStoreName, String masterPassword, boolean isNew) {
		super();
		this.keyStoreName = keyStoreName;
		this.masterPassword = masterPassword;
		this.isNew = isNew;
	}

	public String getKeyStoreName() {
		return keyStoreName;
	}

	public String getMasterPassword() {
		return masterPassword;
	}

	public boolean isNew() {
		return isNew;
	}

}
