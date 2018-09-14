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
import rx.Observable;

import java.util.Collection;

/**
 * Interface for storage of {@link Node}s.
 *
 * @author Andreas Evers
 */
public interface NodeStore {

    String KEY_PREFIX = "virtual:";
    String VIRTUAL_FLAG = "virtual";

    Collection<Node> getAllNodes();

    Observable<Node> getAllNodesAsObservable();

    void saveNode(String nodeData);

    void deleteNode(String nodeId);

    void deleteAllNodes();

    void flushDB();
}
