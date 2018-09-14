/*
 * Copyright 2012-2016 the original author or authors.
 *
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
package be.ordina.msdashboard.nodes.stores;

import be.ordina.msdashboard.nodes.model.Node;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.redis.connection.jedis.JedisConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Tests for {@link RedisStore}
 *
 * @author Andreas Evers
 */
@RunWith(MockitoJUnitRunner.class)
public class RedisStoreTest {

	private static final String nodeAsJson= "{\"id\":\"key1\",\"details\":{\"type\":\"MICROSERVICE\",\"status\":\"UP\"},\"linkedToNodeIds\":[\"test1\",\"test2\"],\"linkedFromNodeIds\":[\"test3\",\"test4\"]}";

	@InjectMocks
	private RedisStore redisService;

	@Mock
	private RedisTemplate<String, Node> redisTemplate;

	@Mock
	private JedisConnectionFactory redisConnectionFactory;

	@Test
	public void getAllNodes() {
		Set<String> keys = Collections.singleton("nodeId");

		doReturn(keys).when(redisTemplate).keys("virtual:*");

		ValueOperations opsForValue = mock(ValueOperations.class);

		doReturn(opsForValue).when(redisTemplate).opsForValue();

		Node node = new Node("redisnode");

		doReturn(node).when(opsForValue).get("nodeId");

		Collection<Node> nodes = redisService.getAllNodes();

		verify(redisTemplate).keys("virtual:*");
		verify(redisTemplate).opsForValue();
		verify(opsForValue).get("nodeId");

		assertThat(nodes).isNotEmpty();
		assertThat(nodes.stream().findFirst().get()).isEqualTo(node);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void saveNode() {
		ValueOperations opsForValue = mock(ValueOperations.class);

		doReturn(opsForValue).when(redisTemplate).opsForValue();

		redisService.saveNode(nodeAsJson);

		verify(redisTemplate).opsForValue();
		verify(opsForValue).set(eq("virtual:key1"), any(Node.class));
	}

	@Test
	public void deleteNode() {
		redisService.deleteNode("nodeId");

		verify(redisTemplate).delete("virtual:nodeId");
	}

	@Test
	public void deleteAllNodes() {
		Set<String> keys = Collections.singleton("nodeId");

		doReturn(keys).when(redisTemplate).keys("*");

		redisService.deleteAllNodes();

		verify(redisTemplate).keys("*");
		verify(redisTemplate).delete(keys);
	}

	@Test
	public void flushDB() {
		JedisConnection redisConnection = mock(JedisConnection.class);

		doReturn(redisConnection).when(redisConnectionFactory).getConnection();

		redisService.flushDB();

		verify(redisConnectionFactory).getConnection();
		verify(redisConnection).flushDb();
	}

}