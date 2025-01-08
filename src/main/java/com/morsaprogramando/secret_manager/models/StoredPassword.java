package com.morsaprogramando.secret_manager.models;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public record StoredPassword(
        String title,
        String username,
        String password,
        Instant createdAt
)  implements Comparable<StoredPassword> {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            .withZone(ZoneId.systemDefault());

    @Override
    public int compareTo(StoredPassword other) {
        return this.title.compareTo(other.title);
    }

    public String createdAtAsString() {
        return formatter.format(this.createdAt);
    }
}
