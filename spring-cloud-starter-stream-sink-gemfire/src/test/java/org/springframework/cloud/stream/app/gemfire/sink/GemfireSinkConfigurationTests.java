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

import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.client.ClientCache;
import com.gemstone.gemfire.cache.client.Pool;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.app.gemfire.config.GemfireSecurityProperties;
import org.springframework.data.gemfire.client.Interest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static junit.framework.TestCase.assertNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

/**
 * @author David Turanski
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest({"gemfire.region.regionName=Stocks", "gemfire.keyExpression='key'",
		"gemfire.pool.hostAddresses=localhost:42424", "gemfire.pool.connectType=server"})
@EnableConfigurationProperties({GemfireSinkProperties.class, GemfireSecurityProperties.class})
public class GemfireSinkConfigurationTests {

	@Resource(name="clientRegion")
	private Region region;

	@Autowired
	private ClientCache clientCache;

	@Autowired
	private  Pool pool;

	@Autowired(required=false)
	private Interest<?> interest;

	@Test
	public void testDefaultConfiguration() {
		assertNull("interest should be null", interest);
		assertThat("subscriptions should not be enabled",!pool.getSubscriptionEnabled());
	}
}
