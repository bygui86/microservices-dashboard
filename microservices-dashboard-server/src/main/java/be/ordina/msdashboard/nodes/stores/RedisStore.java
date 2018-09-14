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

import be.ordina.msdashboard.cache.NodeCache;
import be.ordina.msdashboard.nodes.model.Node;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import rx.Observable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static be.ordina.msdashboard.graph.GraphRetriever.GRAPH_CACHE_NAME;


/**
 * {@link NodeStore} and {@link NodeCache} implemented by Redis.
 *
 * @author Andreas Evers
 */
public class RedisStore implements NodeCache, NodeStore {

	private static final Logger logger = LoggerFactory.getLogger(RedisStore.class);

	private final RedisTemplate<String, Object> redisTemplate;

	private final RedisConnectionFactory redisConnectionFactory;

	@Autowired
	public RedisStore(final RedisTemplate<String, Object> redisTemplate,
	                  final RedisConnectionFactory redisConnectionFactory) {

		if (redisTemplate == null) {
			logger.error("RedisTemplate must not be NULL");
		}

		this.redisTemplate = redisTemplate;

		if (redisConnectionFactory == null) {
			logger.error("RedisConnectionFactory must not be NULL");
		}

		// ((JedisConnectionFactory) redisConnectionFactory).setTimeout(10000);
		this.redisConnectionFactory = redisConnectionFactory;
	}

	@Override
	public Collection<Node> getAllNodes() {

		final List<Node> results = new ArrayList<>();
		final Set<String> keys = redisTemplate.keys(KEY_PREFIX + "*");
		for (final String key : keys) {
			final Node node = (Node) redisTemplate.opsForValue().get(key);
			results.add(node);
		}
		return results;
	}

	@Override
	public Observable<Node> getAllNodesAsObservable() {

		return Observable.from(getAllNodes());
	}

	@Override
	public void saveNode(final String nodeData) {

		logger.info("Saving node: " + nodeData);
		final Node node = getNode(nodeData);
		final String nodeId = node.getId();
		node.getDetails().put(VIRTUAL_FLAG, true);
		redisTemplate.opsForValue().set(KEY_PREFIX + nodeId, node);
		evictGraphCache();
	}

	@Override
	public void deleteNode(final String nodeId) {

		redisTemplate.delete(KEY_PREFIX + nodeId);
		evictGraphCache();
	}

	@Override
	public void deleteAllNodes() {

		redisTemplate.delete(redisTemplate.keys("*"));
		evictGraphCache();
	}

	private Node getNode(final String nodeData) {

		final GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(new ObjectMapper());
		return serializer.deserialize(nodeData.getBytes(), Node.class);
	}

	@Override
	public void flushDB() {

		redisConnectionFactory.getConnection().flushDb();
	}

	@Override
	@CacheEvict(value = GRAPH_CACHE_NAME, allEntries = true)
	public void evictGraphCache() {
		// Intentionally left empty
	}

}