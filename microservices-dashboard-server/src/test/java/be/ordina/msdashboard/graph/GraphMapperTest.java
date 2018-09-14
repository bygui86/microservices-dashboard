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

import be.ordina.msdashboard.nodes.model.Node;
import be.ordina.msdashboard.nodes.model.NodeBuilder;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static be.ordina.msdashboard.nodes.model.NodeTypes.*;
import static be.ordina.msdashboard.graph.GraphRetriever.LINKS;
import static be.ordina.msdashboard.graph.GraphRetriever.NODES;
import static be.ordina.msdashboard.nodes.model.Node.DETAILS;
import static be.ordina.msdashboard.nodes.model.Node.ID;
import static be.ordina.msdashboard.nodes.model.Node.LANE;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link GraphMapper}
 *
 * @author Tim Ysewyn
 */
public class GraphMapperTest {

    @Test
    @SuppressWarnings("unchecked")
    public void shouldReturnGraphMap() {
        Node uiComponentNode = new NodeBuilder().withId("ui").withDetail(Node.TYPE, UI_COMPONENT).withLinkedToNodeId("resource").build();
        Node resourceNode = new NodeBuilder().withId("resource").withDetail(Node.TYPE, RESOURCE).withLinkedToNodeId("microservice").build();
        Node microserviceNode = new NodeBuilder().withId("microservice").withDetail(Node.TYPE, MICROSERVICE).withLinkedToNodeId("unknown node").withLinkedToNodeId("backend").build();
        Node backendNode = new NodeBuilder().withId("backend").withDetail(Node.TYPE, BACKEND).withLinkedFromNodeId("unknown node").build();
        Node unknownNode = new NodeBuilder().withId("virtual node").withLinkedFromNodeId("microservice").build();

        Map<String, Object> graph = GraphMapper.toGraph().call(Arrays.asList(uiComponentNode, resourceNode, microserviceNode, backendNode, unknownNode));

        assertThat(graph).isNotEmpty();
        assertThat(graph).containsKey(NODES);
        assertThat(graph).containsKey(LINKS);

        List<Map<String, Object>> nodes = (List<Map<String, Object>>)graph.get(NODES);
        assertThat(nodes).hasSize(5);

        checkNode(uiComponentNode, nodes.get(0), 0, UI_COMPONENT);
        checkNode(resourceNode, nodes.get(1), 1, RESOURCE);
        checkNode(microserviceNode, nodes.get(2), 2, MICROSERVICE);
        checkNode(backendNode, nodes.get(3), 3, BACKEND);
        checkNode(unknownNode, nodes.get(4), 3);

        Set<Map<String, Integer>> links = (Set<Map<String, Integer>>)graph.get(LINKS);
        assertThat(links).hasSize(4);

        checkLink(links, 0, 1);
        checkLink(links, 1, 2);
        checkLink(links, 2, 3);
        checkLink(links, 2, 4);
    }

    private void checkNode(Node node, Map<String, Object> nodeData, int laneId) {
        assertThat(nodeData).isNotEmpty();
        assertThat(nodeData).containsKeys(ID, LANE);
        assertThat(nodeData.get(ID)).isEqualTo(node.getId());
        assertThat(nodeData.get(LANE)).isEqualTo(laneId);
    }

    @SuppressWarnings("unchecked")
    private void checkNode(Node node, Map<String, Object> nodeData, int laneId, String type) {
        checkNode(node, nodeData, laneId);
        assertThat(nodeData).containsKey(DETAILS);

        Map<String, Object> details = (Map<String, Object>)nodeData.get(DETAILS);
        assertThat(details).containsKey(Node.TYPE);
        assertThat(details.get(Node.TYPE)).isEqualTo(type);
    }

    private void checkLink(Set<Map<String, Integer>> links, int source, int target) {
        boolean setContainsLink = false;

        for (Map<String, Integer> link : links) {
            assertThat(link).containsKeys("source", "target");
            setContainsLink = link.get("source") == source && link.get("target") == target;
            if (setContainsLink)
                break;
        }

        assertThat(setContainsLink).isTrue();
    }

}
