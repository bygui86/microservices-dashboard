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

import be.ordina.msdashboard.nodes.aggregators.NodeAggregator;
import be.ordina.msdashboard.nodes.model.Node;
import be.ordina.msdashboard.nodes.stores.NodeStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import rx.Observable;
import rx.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static be.ordina.msdashboard.nodes.model.Node.LANE;
import static com.google.common.collect.Maps.newHashMap;

/**
 * @author Andreas Evers
 * @author Tim Ysewyn
 */
public class GraphRetriever {

	private static final Logger logger = LoggerFactory.getLogger(GraphRetriever.class);

	public static final String GRAPH_CACHE_NAME = "graph";

	public static final String DIRECTED = "directed";
	public static final String MULTIGRAPH = "multigraph";
	public static final String GRAPH = "graph";
	public static final String LANES = "lanes";
	public static final String TYPES = "types";
	public static final String NODES = "nodes";
	public static final String LINKS = "links";
	
	private final List<NodeAggregator> aggregators;
	private final NodeStore redisService;
	private GraphProperties graphProperties;

	public GraphRetriever(List<NodeAggregator> aggregators, NodeStore redisService,
						  GraphProperties graphProperties) {
		this.aggregators = aggregators;
		this.redisService = redisService;
		this.graphProperties = graphProperties;
	}

	@Cacheable(value = GRAPH_CACHE_NAME, keyGenerator = "simpleKeyGenerator")
	public Map<String, Object> retrieve() {
		List<Observable<Node>> observables = aggregators.stream()
				.collect(Collectors.mapping(NodeAggregator::aggregateNodes, Collectors.toList()));
		observables.add(redisService.getAllNodesAsObservable());

		Map<String, Object> graph = new HashMap<>();
		graph.put(DIRECTED, true);
		graph.put(MULTIGRAPH, false);
		graph.put(GRAPH, new String[0]);
		graph.put(LANES, constructLanes());
		graph.put(TYPES, graphProperties.getTypes());

        Map<String, Object> nodesAndLinks = Observable.mergeDelayError(observables)
					.doOnError(throwable -> logger.error("An error occurred during merging aggregators:", throwable))
					.onErrorResumeNext(Observable.empty())
				.observeOn(Schedulers.io())
					.doOnNext(node -> logger.info("Merging node with id '{}'", node.getId()))
				.reduce(new ArrayList<>(), NodeMerger.merge())
					.doOnError(throwable -> logger.error("An error occurred during reducing:", throwable))
					.onErrorResumeNext(Observable.empty())
					.doOnNext(nodes -> logger.info("Merged all emitted nodes, converting to map"))
                .map(GraphMapper.toGraph())
					.doOnNext(nodesAndLinksMap -> logger.info("Converted to nodes and links map"))
                	.doOnError(throwable -> logger.error("An error occurred during mapping:", throwable))
					.onErrorResumeNext(Observable.empty())
				.toBlocking()
                .first();
		logger.info("Graph retrieved: {}", nodesAndLinks);
        graph.put(NODES, nodesAndLinks.get(NODES));
        graph.put(LINKS, nodesAndLinks.get(LINKS));

		return graph;
	}

	private List<Map<Object, Object>> constructLanes() {
		List<Map<Object, Object>> lanes = new ArrayList<>();
		lanes.add(constructLane(0, graphProperties.getUi()));
		lanes.add(constructLane(1, graphProperties.getResources()));
		lanes.add(constructLane(2, graphProperties.getMicroservices()));
		lanes.add(constructLane(3, graphProperties.getBackends()));
		return lanes;
	}

	private Map<Object, Object> constructLane(final int lane, final String type) {
		Map<Object, Object> laneMap = newHashMap();
		laneMap.put(LANE, lane);
		laneMap.put(Node.TYPE, type);
		return laneMap;
	}


}