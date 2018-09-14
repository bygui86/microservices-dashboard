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

package be.ordina.msdashboard.graph;

import be.ordina.msdashboard.nodes.aggregators.health.HealthIndicatorsAggregator;
import be.ordina.msdashboard.nodes.aggregators.index.IndexesAggregator;
import be.ordina.msdashboard.nodes.aggregators.pact.PactsAggregator;
import be.ordina.msdashboard.nodes.stores.NodeStore;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import rx.Observable;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Map;

import static be.ordina.msdashboard.JsonHelper.load;
import static be.ordina.msdashboard.JsonHelper.removeBlankNodes;
import static be.ordina.msdashboard.graph.GraphProperties.DB;
import static be.ordina.msdashboard.graph.GraphProperties.JMS;
import static be.ordina.msdashboard.graph.GraphProperties.REST;
import static be.ordina.msdashboard.graph.GraphProperties.SOAP;
import static be.ordina.msdashboard.nodes.model.NodeBuilder.node;
import static be.ordina.msdashboard.nodes.model.NodeTypes.MICROSERVICE;
import static be.ordina.msdashboard.nodes.model.NodeTypes.RESOURCE;
import static com.google.common.collect.Sets.newHashSet;
import static org.mockito.Mockito.when;


/**
 * Tests for {@link GraphRetriever}
 *
 * @author Andreas Evers
 * @author Tim Ysewyn
 */
@RunWith(MockitoJUnitRunner.class)
public class GraphRetrieverTest {

	private static final Logger logger = LoggerFactory.getLogger(GraphRetrieverTest.class);

	private GraphRetriever graphRetriever;

	@Mock
	private HealthIndicatorsAggregator healthIndicatorsAggregator;

	@Mock
	private IndexesAggregator indexesAggregator;

	@Mock
	private PactsAggregator pactsAggregator;

	@Mock
	private NodeStore redisService;

	@Mock
	private GraphProperties graphProperties;

	@Before
	public void setUp() {

		graphRetriever = new GraphRetriever(Arrays.asList(healthIndicatorsAggregator, indexesAggregator, pactsAggregator), redisService, graphProperties);
	}

	@Test
	public void retrieveGraph() throws FileNotFoundException, UnsupportedEncodingException, JSONException {

		when(healthIndicatorsAggregator.aggregateNodes())
				.thenReturn(Observable.from(newHashSet(
						node().withId("service1").havingLinkedToNodeIds(newHashSet("backend1")).build(),
						node().withId("service2").havingLinkedToNodeIds(newHashSet("backend2")).build(),
						node().withId("backend1").havingLinkedFromNodeIds(newHashSet("service1")).build(),
						node().withId("backend2").havingLinkedFromNodeIds(newHashSet("service2")).build())));
		when(indexesAggregator.aggregateNodes())
				.thenReturn(Observable.from(newHashSet(
						node().withId("svc1rsc1").havingLinkedToNodeIds(newHashSet("service1")).build(),
						node().withId("svc1rsc2").havingLinkedToNodeIds(newHashSet("service1")).build(),
						node().withId("svc2rsc1").havingLinkedToNodeIds(newHashSet("service2")).build(),
						node().withId("service1").withDetail("test", "test").build(),
						node().withId("service2").build())));
		when(pactsAggregator.aggregateNodes())
				.thenReturn(Observable.from(newHashSet(
						node().withId("svc1rsc2").build(),
						node().withId("svc2rsc1").build(),
						node().withId("service1").havingLinkedToNodeIds(newHashSet("svc2rsc1")).build(),
						node().withId("service2").havingLinkedToNodeIds(newHashSet("svc1rsc2")).build())));
		when(redisService.getAllNodes())
				.thenReturn(newHashSet());
		when(graphProperties.getUi()).thenReturn("UI Components");
		when(graphProperties.getResources()).thenReturn("Resources");
		when(graphProperties.getMicroservices()).thenReturn("Microservices");
		when(graphProperties.getBackends()).thenReturn("Backends");
		when(graphProperties.getTypes()).thenReturn(Arrays.asList(DB, MICROSERVICE, REST, SOAP, JMS, RESOURCE));

		final long startTime = System.currentTimeMillis();
		final Map<String, Object> graph = graphRetriever.retrieve();
		final long totalTime = System.currentTimeMillis() - startTime;

		logger.info("Time spent waiting for processing: " + totalTime);
		final GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(new ObjectMapper());
		final String nodeAsJson = new String(serializer.serialize(graph), "UTF-8");
		JSONAssert.assertEquals(removeBlankNodes(load("src/test/resources/GraphRetrieverTest.json")),
				removeBlankNodes(nodeAsJson), JSONCompareMode.LENIENT);
	}
}