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
package be.ordina.msdashboard.nodes.model;

import java.util.*;

/**
 * Builder for a {@link Node}.
 *
 * @author Andreas Evers
 */
public class NodeBuilder {

	private String id;
	private Map<String, Object> details;
	private Integer lane;
	private Set<String> linkedToNodeIds;
	private Set<String> linkedFromNodeIds;

	public NodeBuilder withId(final String id) {
		this.id = id;
		return this;
	}

	public NodeBuilder withLane(final Integer lane) {
		this.lane = lane;
		return this;
	}

	public NodeBuilder havingDetails(final Map<String, Object> details) {
		this.details = details;
		return this;
	}

	public NodeBuilder havingLinkedToNodeIds(final Set<String> linkedToNodeIds) {
		this.linkedToNodeIds = linkedToNodeIds;
		return this;
	}

	public NodeBuilder havingLinkedFromNodeIds(final Set<String> linkedFromNodeIds) {
		this.linkedFromNodeIds = linkedFromNodeIds;
		return this;
	}

	public NodeBuilder withDetail(final String key, final String value) {
		if (details == null) {
			details = new HashMap<>();
		}
		details.put(key, value);
		return this;
	}

	public NodeBuilder withLinkedToNodeId(final String nodeId) {
		if (linkedToNodeIds == null) {
			linkedToNodeIds = new HashSet<>();
		}
		linkedToNodeIds.add(nodeId);
		return this;
	}

	public NodeBuilder withLinkedFromNodeId(final String nodeId) {
		if (linkedFromNodeIds == null) {
			linkedFromNodeIds = new HashSet<>();
		}
		linkedFromNodeIds.add(nodeId);
		return this;
	}

	public Node build() {
		Node node = new Node(id);
		node.setLane(lane);
		node.setLinkedToNodeIds(linkedToNodeIds);
		node.setLinkedFromNodeIds(linkedFromNodeIds);
		node.setDetails(details);
		return node;
	}

	public static NodeBuilder node() {
		return new NodeBuilder();
	}
}