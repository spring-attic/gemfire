/*
 * Copyright 2015-2018 the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.stream.app.gemfire.sink.ssl;

import java.io.IOException;

import javax.annotation.Resource;

import org.apache.geode.cache.Region;
import org.apache.geode.pdx.PdxInstance;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.app.gemfire.JsonObjectTransformer;
import org.springframework.cloud.stream.app.gemfire.sink.GemfireSinkConfiguration;
import org.springframework.cloud.stream.app.test.gemfire.process.GeodeServerLauncherHelper;
import org.springframework.cloud.stream.app.test.gemfire.process.ProcessWrapper;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;


/**
 * @author David Turanski
 * @author Christian Tzolov
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE,
		properties = { "gemfire.region.regionName=Stocks",
				"gemfire.keyExpression='key'",
				"gemfire.pool.hostAddresses=localhost:42425",
				"gemfire.pool.connectType=server",
				"gemfire.security.ssl.truststoreUri=classpath:/trusted.keystore",
				"gemfire.security.ssl.keystoreUri=classpath:/trusted.keystore",
				"gemfire.security.ssl.sslKeystorePassword=password",
				"gemfire.security.ssl.sslTruststorePassword=password",
				"spring.cloud.stream.default.binder=test",
		},
		classes = {GemfireSinkConfiguration.class})
@DirtiesContext
public abstract class SslGemfireSinkIntegrationTests {

	@Autowired
	protected Sink gemfireSink;

	protected final JsonObjectTransformer transformer = new JsonObjectTransformer();

	@Resource(name = "clientRegion")
	Region<String, String> region;

	private static ProcessWrapper serverProcess;

	@BeforeClass
	public static void setup() throws IOException {
		serverProcess = GeodeServerLauncherHelper.startGeode("SslGemFireTestServer", "ssl-gemfire-server.xml");
	}

	@TestPropertySource(properties = { "gemfire.json=false"})
	public static class GemfireSinkNonJsonModeTests extends SslGemfireSinkIntegrationTests {

		@Test
		public void test() {
			gemfireSink.input().send(new GenericMessage("hello"));
			assertThat(region.get("key"), equalTo("hello"));
		}
	}

	@TestPropertySource(properties = "gemfire.json=true")
	public static class GemfireSinkJsonModeTests extends SslGemfireSinkIntegrationTests {

		@Test
		public void testByteArrayJsonPayload() {
			String messageBody = "{\"first\":\"second\"}";
			gemfireSink.input().send(new GenericMessage(messageBody.getBytes()));
			assertThat(region.get("key"), instanceOf(PdxInstance.class));
			assertThat(transformer.toString(region.get("key")), equalTo(messageBody));
		}

		@Test
		public void testStringJsonPayload() {
			String messageBody = "{\"foo\":\"bar\"}";
			gemfireSink.input().send(new GenericMessage(messageBody));
			assertThat(region.get("key"), instanceOf(PdxInstance.class));
			assertThat(transformer.toString(region.get("key")), equalTo(messageBody));
		}
	}

	@AfterClass
	public static void tearDown() {
		GeodeServerLauncherHelper.tearDown(serverProcess);
		while (serverProcess.isRunning()) {
			try {
				Thread.sleep(1000);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}
}
