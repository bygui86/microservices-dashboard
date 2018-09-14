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
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;

import java.io.UnsupportedEncodingException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link SimpleStore}
 *
 * @author Tim De Bruyn
 */
@RunWith(MockitoJUnitRunner.class)
public class SimpleStoreTest {

    @InjectMocks
    private SimpleStore simpleStore;
    
    @Test
	public void savingAndDeletingNodes() throws UnsupportedEncodingException {
		GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(new ObjectMapper());

		Node node1 = new Node("some id");
		simpleStore.saveNode(new String(serializer.serialize(node1), "UTF-8"));
		assertThat(simpleStore.getAllNodes().size()).isEqualTo(1);

		Node node2 = new Node("another id");
		simpleStore.saveNode(new String(serializer.serialize(node2), "UTF-8"));
		simpleStore.deleteNode("some id");
		assertThat(simpleStore.getAllNodes().iterator().next().getId()).isEqualTo("another id");

		simpleStore.deleteAllNodes();
		assertThat(simpleStore.getAllNodes().size()).isEqualTo(0);

		Node node3 = new Node("again another id");
		simpleStore.saveNode(new String(serializer.serialize(node3), "UTF-8"));
		assertThat(simpleStore.getAllNodesAsObservable()).isNotNull();

		simpleStore.flushDB();
		assertThat(simpleStore.getAllNodes().size()).isEqualTo(0);
	}
}
