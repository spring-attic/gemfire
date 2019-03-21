/*
 * Copyright (c) 2016 the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License") ;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.stream.app.gemfire.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.gemfire.client.ClientCacheFactoryBean;
import org.springframework.util.StringUtils;

import java.util.Properties;

/**
 * @author David Turanski
 **/

@EnableConfigurationProperties(GemfireSecurityProperties.class)
public class GemfireClientCacheConfiguration {

	private static final String SECURITY_CLIENT = "security-client-auth-init";
	private static final String SECURITY_USERNAME = "security-username";
	private static final String SECURITY_PASSWORD = "security-password";

	@Bean
	public ClientCacheFactoryBean clientCache(GemfireSecurityProperties securityProperties) {
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

		clientCacheFactoryBean.setReadyForEvents(true);

		return clientCacheFactoryBean;
	}
}
