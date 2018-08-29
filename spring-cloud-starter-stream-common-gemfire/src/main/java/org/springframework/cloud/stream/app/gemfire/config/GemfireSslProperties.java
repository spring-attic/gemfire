/*
 * Copyright 2018 the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.stream.app.gemfire.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;

/**
 * @author Christian Tzolov
 */
@ConfigurationProperties("gemfire.security.ssl")
public class GemfireSslProperties {

	private static final String USER_HOME_DIRECTORY = System.getProperty("user.home");

	/**
	 * Location of the pre-created truststore file to be used for connecting to the Geode cluster
	 */
	private Resource truststoreUri;

	/**
	 * Name of the trust store file copied in the local file system.
	 */
	private String storeFileName = "trusted.keystore";

	/**
	 * Password for accessing the keys truststore
	 */
	private String sslKeystorePassword;

	/**
	 * Password for accessing the trust store
	 */
	private String sslTruststorePassword;

	/**
	 * Local location to copy the trust store file.
	 */
	private String userHomeDirectory = USER_HOME_DIRECTORY;

	public Resource getTruststoreUri() {
		return truststoreUri;
	}

	public void setTruststoreUri(Resource truststoreUri) {
		this.truststoreUri = truststoreUri;
	}

	public String getStoreFileName() {
		return storeFileName;
	}

	public void setStoreFileName(String storeFileName) {
		this.storeFileName = storeFileName;
	}

	public String getSslKeystorePassword() {
		return sslKeystorePassword;
	}

	public void setSslKeystorePassword(String sslKeystorePassword) {
		this.sslKeystorePassword = sslKeystorePassword;
	}

	public String getSslTruststorePassword() {
		return sslTruststorePassword;
	}

	public void setSslTruststorePassword(String sslTruststorePassword) {
		this.sslTruststorePassword = sslTruststorePassword;
	}

	public String getUserHomeDirectory() {
		return userHomeDirectory;
	}

	public void setUserHomeDirectory(String userHomeDirectory) {
		this.userHomeDirectory = userHomeDirectory;
	}
}
