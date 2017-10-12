package org.springframework.cloud.stream.app.gemfire.config;

import java.util.Map;

/**
 * @author David Turanski
 **/
public class GemfireUser {

	private String username;
	private String password;

	public GemfireUser(Map<String, String> map) {
		username = map.get("username");
		password = map.get("password");
	}

	public boolean isDeveloper() {
		return username != null && username.equals("developer");
	}

	public String getPassword() {
		return password;
	}

	public String getUsername() {
		return username;
	}
}
