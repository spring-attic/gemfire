package org.springframework.cloud.stream.app.gemfire.config;

/**
 * @author David Turanski
 **/

import org.springframework.cloud.service.BaseServiceInfo;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServiceInfo extends BaseServiceInfo {

	private final Pattern p = Pattern.compile("(.*)\\[(\\d*)\\]");

	private URI[] locators;

	private final GemfireUser user;

	public ServiceInfo(String id, List<String> locators, List<Map<String, String>> users) {
		super(id);

		parseLocators(locators);
		user = getDefaultUser(users);
	}

	private void parseLocators(List<String> locators) {
		ArrayList<URI> uris = new ArrayList<URI>(locators.size());

		for (String locator : locators) {
			uris.add(parseLocator(locator));
		}

		this.locators = uris.toArray(new URI[uris.size()]);
	}

	private URI parseLocator(String locator) throws IllegalArgumentException {
		Matcher m = p.matcher(locator);
		if (!m.find()) {
			throw new IllegalArgumentException(
				"Could not parse locator url. Expected format host[port], received: " + locator);
		}
		else {
			if (m.groupCount() != 2) {
				throw new IllegalArgumentException(
					"Could not parse locator url. Expected format host[port], received: " + locator);
			}
			try {
				return new URI("locator://" + m.group(1) + ":" + m.group(2));
			}
			catch (URISyntaxException e) {
				throw new IllegalArgumentException("Malformed URL " + locator);
			}
		}
	}

	private GemfireUser getDefaultUser(List<Map<String, String>> users) {
		GemfireUser user = null;

		for (Map<String, String> map : users) {
			GemfireUser u = new GemfireUser(map);
			if (user.isDeveloper()) {
				user = u;
			}
		}
		return user;
	}

	public URI[] getLocators() {
		return locators;
	}

	public String getUsername() {
		return user != null ? user.getUsername() : null;
	}

	public String getPassword() {
		return user != null ? user.getPassword() : null;
	}

}

