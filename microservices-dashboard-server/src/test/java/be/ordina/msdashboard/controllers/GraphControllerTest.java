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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

/**
 * Tests for {@link GraphController}
 *
 * @author Tim Ysewyn
 */
@RunWith(MockitoJUnitRunner.class)
public class GraphControllerTest {

    @InjectMocks
    private GraphController graphController;
    @Mock
    private GraphRetriever graphRetriever;
    @Mock
    private NodeStore nodeStore;

    @Test
    public void getDependenciesGraphJson() {
        doReturn(Collections.emptyMap()).when(graphRetriever).retrieve();

        HttpEntity<Map<String, Object>> httpEntity = graphController.retrieveGraph();

        assertThat(httpEntity.getBody()).isEmpty();
    }

    @Test
    public void saveNode() {
        graphController.saveNode("nodeAsJson");

        verify(nodeStore).saveNode("nodeAsJson");
    }

    @Test
    public void getAllNodes() {
        doReturn(Collections.emptyList()).when(nodeStore).getAllNodes();

        Collection<Node> nodes = graphController.getAllNodes();

        assertThat(nodes).isEmpty();
    }

    @Test
    public void deleteNode() {
        graphController.deleteNode("nodeId");

        verify(nodeStore).deleteNode("nodeId");
    }

    @Test
    public void deleteAllNodes() {
        graphController.deleteAllNodes();

        verify(nodeStore).deleteAllNodes();
    }

    @Test
    public void flushAll() {
        graphController.flushAll();

        verify(nodeStore).flushDB();
    }

}
