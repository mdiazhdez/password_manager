package com.morsaprogramando.secret_manager.models;

public record StoredPassword(
        String title,
        String username,
        String password
) {
}
