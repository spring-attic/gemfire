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

package org.springframework.cloud.stream.app.gemfire.cq.source;

import com.gemstone.gemfire.cache.client.Pool;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.test.binder.TestSupportBinderAutoConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author David Turanski
 **/
@RunWith(SpringJUnit4ClassRunner.class)

@SpringBootTest( classes = {
		GemfireCqSourceConfiguration.class,
		PropertyPlaceholderAutoConfiguration.class,
		TestSupportBinderAutoConfiguration.class }, value = { "gemfire.query= Select * from /Stocks"} )
@EnableConfigurationProperties(GemfireCqSourceProperties.class)
public class GemfireCqSourceConfigurationTests {

	@Autowired
	private Pool pool;

	@Test
	@Ignore("No Subscription Servers available")
	public void testDefaultConfiguration() throws InterruptedException {
		assertThat("subscriptions should be enabled", pool.getSubscriptionEnabled());
	}

}
