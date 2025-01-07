package com.morsaprogramando.secret_manager.models;

public record KeystoreData(
        String keyStoreName,
        String masterPassword,
        boolean isNew
) {
}
