/*
 * Copyright (c) 2016-2018 the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License") ;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.stream.app.gemfire.config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.gemfire.client.ClientCacheFactoryBean;
import org.springframework.data.gemfire.util.PropertiesBuilder;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;

/**
 * @author David Turanski
 * @author Christian Tzolov
 */

@EnableConfigurationProperties({ GemfireSecurityProperties.class, GemfireSslProperties.class })
public class GemfireClientCacheConfiguration {

	private static final String SECURITY_CLIENT = "security-client-auth-init";
	private static final String SECURITY_USERNAME = "security-username";
	private static final String SECURITY_PASSWORD = "security-password";

	@Bean
	public ClientCacheFactoryBean clientCache(GemfireSecurityProperties securityProperties, GemfireSslProperties sslProperties) {
		ClientCacheFactoryBean clientCacheFactoryBean = new ClientCacheFactoryBean();
		clientCacheFactoryBean.setUseBeanFactoryLocator(false);
		clientCacheFactoryBean.setPoolName("gemfirePool");

		if (StringUtils.hasText(securityProperties.getUsername()) && StringUtils
				.hasText(securityProperties.getPassword())) {
			Properties properties = new Properties();
			properties
					.setProperty(SECURITY_CLIENT, GemfireSecurityProperties.UserAuthInitialize.class.getName() + ".create");
			properties.setProperty(SECURITY_USERNAME, securityProperties.getUsername());
			properties.setProperty(SECURITY_PASSWORD, securityProperties.getPassword());
			clientCacheFactoryBean.setProperties(properties);
		}

		if (sslProperties.getTruststoreUri() != null) {
			PropertiesBuilder pb = new PropertiesBuilder();
			pb.add(clientCacheFactoryBean.getProperties());
			pb.add(this.toGeodeSslProperties(sslProperties));
			clientCacheFactoryBean.setProperties(pb.build());
		}

		clientCacheFactoryBean.setReadyForEvents(true);

		return clientCacheFactoryBean;
	}

	/**
	 * Converts the App Starter properties into Geode native SSL properties
	 * @param sslProperties App starter properties.
	 * @return Returns the geode native SSL properties.
	 */
	private Properties toGeodeSslProperties(GemfireSslProperties sslProperties) {

		String trustKeyStorePath = this.resolveTrustedKeystore(sslProperties);
		PropertiesBuilder pb = new PropertiesBuilder();
		pb.setProperty("ssl-enabled-components", "server,locator");
		pb.setProperty("ssl-keystore", trustKeyStorePath);
		pb.setProperty("ssl-keystore-password", sslProperties.getSslKeystorePassword());
		pb.setProperty("ssl-truststore", trustKeyStorePath);
		pb.setProperty("ssl-truststore-password", sslProperties.getSslTruststorePassword());

		return pb.build();
	}

	/**
	 * Copy the Trust store specified in the URI into a local accessible file.
	 * @param sslProperties SSL gemfire properties required for retrieving the provided truststore.
	 * @return Returns the absolute path of the local trust store copy
	 */
	private String resolveTrustedKeystore(GemfireSslProperties sslProperties) {

		File trustStoreFile = new File(sslProperties.getUserHomeDirectory(), sslProperties.getStoreFileName());
		try {
			FileCopyUtils.copy(sslProperties.getTruststoreUri().getInputStream(), new FileOutputStream(trustStoreFile));
			return trustStoreFile.getAbsolutePath();
		}
		catch (IOException e) {
			throw new IllegalStateException(String.format("Failed to copy the trust store from [%s] into %s",
					sslProperties.getTruststoreUri().getDescription(), trustStoreFile.getAbsolutePath()), e);
		}
	}
}
