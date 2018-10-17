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

package org.springframework.cloud.stream.app.gemfire.source;

import java.io.IOException;
import java.util.List;

import org.apache.geode.cache.client.Pool;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.app.test.gemfire.process.GeodeServerLauncherHelper;
import org.springframework.cloud.stream.app.test.gemfire.process.ProcessWrapper;
import org.springframework.cloud.stream.test.binder.TestSupportBinderAutoConfiguration;
import org.springframework.data.gemfire.client.Interest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author David Turanski
 * @author Christian Tzolov
 */
@RunWith(SpringJUnit4ClassRunner.class)

@SpringBootTest(classes = { GemfireSourceConfiguration.class,
		PropertyPlaceholderAutoConfiguration.class,
		TestSupportBinderAutoConfiguration.class }, value = { "gemfire.region.regionName=Stocks" })
@EnableConfigurationProperties(GemfireSourceProperties.class)
@DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
public class GemfireSourceConfigurationTests {

	@Autowired
	private Pool pool;

	@Autowired
	private List<Interest> interests;

	private static ProcessWrapper serverProcess;

	@BeforeClass
	public static void setup() throws IOException {
		serverProcess = GeodeServerLauncherHelper.startGeode("GemFireTestServer", "gemfire-server.xml");
	}

	@Test
	@Ignore("No Subscription Servers available")
	public void testDefaultConfiguration() {
		assertThat("interests not present", interests.size() >= 1);
		assertThat("subscriptions should be enabled", pool.getSubscriptionEnabled());
	}

	@AfterClass
	public static void tearDown() {
		GeodeServerLauncherHelper.tearDown(serverProcess);
	}

}
