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
package be.ordina.msdashboard.nodes.aggregators.health;

import be.ordina.msdashboard.nodes.model.Node;
import org.junit.Test;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import static be.ordina.msdashboard.nodes.model.NodeTypes.MICROSERVICE;
import static be.ordina.msdashboard.nodes.model.Node.TYPE;
import static be.ordina.msdashboard.nodes.aggregators.Constants.CONFIG_SERVER;
import static be.ordina.msdashboard.nodes.aggregators.Constants.DISCOVERY;
import static be.ordina.msdashboard.nodes.aggregators.health.MicroserviceGrouper.GROUP;
import static be.ordina.msdashboard.nodes.aggregators.health.MicroserviceGrouper.SPRING_CLOUD;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Tests for {@link MicroserviceGrouper}
 *
 * @author Tim De Bruyn
 */
public class MicroserviceGrouperTest {

	private MicroserviceGrouper microserviceGrouper = new MicroserviceGrouper();
	
	@Test
	public void emptySourceList() {
		Collection<Node> modified = microserviceGrouper.enrich(emptyList());
		assertNotNull(modified);
	}
	
	@Test
	public void noMicroServiceNode() {
		Collection<Node> source = newHashSet(new Node("some id"), new Node("some other id"));
		Collection<Node> modified = microserviceGrouper.enrich(source);
		assertEquals(source, modified);
	}
	
	@Test
	public void someMicroserviceNodes() {
		Collection<Node> source = newHashSet(new Node(DISCOVERY), new Node("some id"), new Node(CONFIG_SERVER));
		HashSet<Node> modified = (HashSet<Node>) microserviceGrouper.enrich(source);
		assertEquals(3, modified.size());
		Iterator<Node> nodes = modified.iterator();
		
		Node node1 = nodes.next();
		assertNotEquals(MICROSERVICE, node1.getDetails().get(TYPE));
		assertNotEquals(SPRING_CLOUD, node1.getDetails().get(GROUP), SPRING_CLOUD);
		
		Node node2 = nodes.next();
		assertEquals(MICROSERVICE, node2.getDetails().get(TYPE));
		assertEquals(SPRING_CLOUD, node2.getDetails().get(GROUP));

		Node node3 = nodes.next();
		assertEquals(MICROSERVICE, node3.getDetails().get(TYPE));
		assertEquals(SPRING_CLOUD, node3.getDetails().get(GROUP));
	}
}
