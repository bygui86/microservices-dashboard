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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.functions.Func2;

import java.util.List;
import java.util.Optional;

/**
 * @author Tim Ysewyn
 */
public class NodeMerger {

    private static final Logger logger = LoggerFactory.getLogger(NodeMerger.class);

    public static Func2<List<Node>, Node, List<Node>> merge() {
        return (mergedNodes, node) -> {

            // TODO: we should be able to enrich nodes in a general way before merging, eg. convert all service names to lowercase
            // Aggregator specific logic should not come here, eg. removing the Eureka description

            Optional<Integer> nodeIndex = mergedNodes.stream()
                    .filter(n -> n.getId().equalsIgnoreCase(node.getId()))
                    .map(mergedNodes::indexOf)
                    .findFirst();

            if (nodeIndex.isPresent()) {
                logger.info("Node with id '{}' previously added, merging", node.getId());
                mergedNodes.get(nodeIndex.get()).mergeWith(node);
            } else {
                logger.info("Node with id '{}' was not merged before, adding it to the list", node.getId());
                mergedNodes.add(node);
            }

            return mergedNodes;
        };
    }
}
