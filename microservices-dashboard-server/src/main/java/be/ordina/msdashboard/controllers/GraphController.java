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
package be.ordina.msdashboard.controllers;

import be.ordina.msdashboard.graph.GraphRetriever;
import be.ordina.msdashboard.nodes.model.Node;
import be.ordina.msdashboard.nodes.stores.NodeStore;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Map;

import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * @author Andreas Evers
 */
@CrossOrigin(maxAge = 3600)
@RestController
@ResponseBody
public class GraphController {

	private GraphRetriever graphRetriever;
	private NodeStore nodeStore;

	public GraphController(GraphRetriever graphRetriever, NodeStore nodeStore) {
		this.graphRetriever = graphRetriever;
		this.nodeStore = nodeStore;
	}

	//TODO: Support table response?
	@RequestMapping(value = "/graph", produces = "application/json")
	public HttpEntity<Map<String, Object>> retrieveGraph() {
		return ok().body(graphRetriever.retrieve());
	}

	@RequestMapping(value = "/node", method = POST)
	public void saveNode(@RequestBody final String nodeData) {
		nodeStore.saveNode(nodeData);
	}

	@RequestMapping(value = "/node", method = GET)
	public Collection<Node> getAllNodes() {
		return nodeStore.getAllNodes();
	}

	@RequestMapping(value = "/node/{nodeId}", method = DELETE)
	public void deleteNode(@PathVariable String nodeId) {
		nodeStore.deleteNode(nodeId);
	}

	@RequestMapping(value = "/node", method = DELETE)
	public void deleteAllNodes() {
		nodeStore.deleteAllNodes();
	}

	@RequestMapping(value = "/flush", method = DELETE)
	public void flushAll() {
		nodeStore.flushDB();
	}
}
