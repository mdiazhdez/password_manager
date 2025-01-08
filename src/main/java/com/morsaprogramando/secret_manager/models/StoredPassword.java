package com.morsaprogramando.secret_manager.models;

import java.time.Instant;

public record StoredPassword(
        String title,
        String username,
        String password,
        Instant createdAt
)  implements Comparable<StoredPassword> {

    @Override
    public int compareTo(StoredPassword other) {
        return this.title.compareTo(other.title);
    }
}
