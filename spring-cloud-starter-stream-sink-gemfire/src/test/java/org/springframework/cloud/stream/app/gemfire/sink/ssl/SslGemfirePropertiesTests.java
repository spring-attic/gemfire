/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.stream.app.gemfire.sink.ssl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.springframework.boot.context.properties.ConfigurationPropertiesBindException;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.cloud.stream.app.gemfire.config.GemfireSslProperties;
import org.springframework.cloud.stream.config.SpelExpressionConverterConfiguration;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

/**
 * @author Christian Tzolov
 */
public class SslGemfirePropertiesTests {

	private AnnotationConfigApplicationContext context;

	@Before
	public void beforeTest() {
		context = new AnnotationConfigApplicationContext();
	}

	@After
	public void afterTest() {
		context.close();
	}

	@Test
	public void defaultProperties() {
		GemfireSslProperties properties = withProperties();
		assertThat(properties.getUserHomeDirectory(), equalTo(System.getProperty("user.home")));
		assertThat(properties.getTruststoreUri(), equalTo(null));
		assertThat(properties.getKeystoreUri(), equalTo(null));
		assertThat(properties.getTruststoreType(), equalTo("JKS"));
		assertThat(properties.getKeystoreType(), equalTo("JKS"));
		assertThat(properties.getSslKeystorePassword(), equalTo(null));
		assertThat(properties.getSslTruststorePassword(), equalTo(null));
		assertThat(properties.getCiphers(), equalTo("any"));
	}

	@Test
	public void customProperties() {
		GemfireSslProperties properties = withProperties(
				"gemfire.security.ssl.userHomeDirectory:/custom_home",
				"gemfire.security.ssl.truststoreUri:/custom_truststoreUri",
				"gemfire.security.ssl.keystoreUri:/custom_keystoreUri",
				"gemfire.security.ssl.truststoreType:/custom_TruststoreType",
				"gemfire.security.ssl.keystoreType:/custom_KeystoreType",
				"gemfire.security.ssl.sslKeystorePassword:/custom_SslKeystorePassword",
				"gemfire.security.ssl.SslTruststorePassword:/custom_SslTruststorePassword",
				"gemfire.security.ssl.ciphers:/custom_any");

		assertThat(properties.getUserHomeDirectory(), equalTo("/custom_home"));
		assertThat(properties.getTruststoreUri(), equalTo(context.getResource("/custom_truststoreUri")));
		assertThat(properties.getKeystoreUri(), equalTo(context.getResource("/custom_keystoreUri")));
		assertThat(properties.getTruststoreType(), equalTo("/custom_TruststoreType"));
		assertThat(properties.getKeystoreType(), equalTo("/custom_KeystoreType"));
		assertThat(properties.getSslKeystorePassword(), equalTo("/custom_SslKeystorePassword"));
		assertThat(properties.getSslTruststorePassword(), equalTo("/custom_SslTruststorePassword"));
		assertThat(properties.getCiphers(), equalTo("/custom_any"));

	}

	@Test(expected = ConfigurationPropertiesBindException.class)
	public void emptyUserHomeDirectory() {
			withProperties("gemfire.security.ssl.userHomeDirectory:");
	}

	@Test(expected = ConfigurationPropertiesBindException.class)
	public void emptyTruststoreType() {
		withProperties("gemfire.security.ssl.truststoreType:");
	}

	@Test(expected = ConfigurationPropertiesBindException.class)
	public void emptyKeystoreType() {
		withProperties("gemfire.security.ssl.keystoreType:");
	}

	@Test(expected = ConfigurationPropertiesBindException.class)
	public void emptyCiphers() {
		withProperties("gemfire.security.ssl.ciphers:");
	}

	@Test(expected = ConfigurationPropertiesBindException.class)
	public void truststoreUriWithoutKeystoreUri() {
		withProperties("gemfire.security.ssl.truststoreUri:classpath:/trusted.keystore");
	}

	@Test(expected = ConfigurationPropertiesBindException.class)
	public void keystoreUriWithoutTruststoreUri() {
		withProperties("gemfire.security.ssl.keystoreUri:classpath:/trusted.keystore");
	}

	@Test
	public void keystoreUriAndTruststoreUri() {
		GemfireSslProperties properties = withProperties("gemfire.security.ssl.truststoreUri:classpath:/trusted.keystore",
				"gemfire.security.ssl.keystoreUri:classpath:/trusted.keystore",
				"gemfire.security.ssl.sslKeystorePassword:/custom_SslKeystorePassword",
				"gemfire.security.ssl.SslTruststorePassword:/custom_SslTruststorePassword");
		assertThat(properties.getTruststoreUri(), equalTo(context.getResource("classpath:/trusted.keystore")));
		assertThat(properties.getKeystoreUri(), equalTo(context.getResource("classpath:/trusted.keystore")));
		assertThat(properties.isSslEnabled(), equalTo(true));

	}

	private GemfireSslProperties withProperties(String... inputProperties) {
		TestPropertyValues.of(inputProperties).applyTo(context);
		context.register(Conf.class);
		context.refresh();
		GemfireSslProperties properties = context.getBean(GemfireSslProperties.class);
		return properties;
	}

	@Configuration
	@EnableConfigurationProperties(GemfireSslProperties.class)
	@Import(SpelExpressionConverterConfiguration.class)
	static class Conf {

	}

}
