package com.morsaprogramando.secret_manager.models;

public record StoredPassword(
        String username,
        String password
) {
}
