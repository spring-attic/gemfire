/*
 * Copyright (c) 2016 the original author or authors.
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

package org.springframework.cloud.stream.app.gemfire.sink;

import javax.annotation.Resource;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.Pool;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.gemfire.client.Interest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static junit.framework.TestCase.assertNull;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author David Turanski
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(value = { "gemfire.region.regionName=Stocks", "gemfire.sink.keyExpression='key'",
		"gemfire.pool.hostAddresses=localhost:42424", "gemfire.pool.connectType=server",
		"spring.cloud.stream.default.binder=test" },
		classes = { GemfireSinkConfiguration.class })
public class GemfireSinkConfigurationTests {

	@Resource(name = "clientRegion")
	Region<String, String> region;

	@Autowired
	private ClientCache clientCache;

	@Autowired
	private Pool pool;

	@Autowired(required = false)
	private Interest<?> interest;

	@Test
	public void testDefaultConfiguration() {
		assertNull("interest should be null", interest);
		assertThat("subscriptions should not be enabled", !pool.getSubscriptionEnabled());
	}
}
