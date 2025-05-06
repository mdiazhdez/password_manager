package com.morsaprogramando.secret_manager.models;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class StoredPassword implements Comparable<StoredPassword> {
	private final String title;
	private final String username;
	private final String password;
	private final Instant createdAt;

	public StoredPassword(String title, String username, String password, Instant createdAt) {
		super();
		this.title = title;
		this.username = username;
		this.password = password;
		this.createdAt = createdAt;
	}

	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
			.withZone(ZoneId.systemDefault());

	@Override
	public int compareTo(StoredPassword other) {
		return this.title.compareTo(other.title);
	}

	public String createdAtAsString() {
		return formatter.format(this.createdAt);
	}

	public String getTitle() {
		return title;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public static DateTimeFormatter getFormatter() {
		return formatter;
	}

}
