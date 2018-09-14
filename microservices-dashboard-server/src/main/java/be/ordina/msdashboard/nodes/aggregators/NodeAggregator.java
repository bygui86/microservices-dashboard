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
package be.ordina.msdashboard.nodes.aggregators;

import be.ordina.msdashboard.nodes.model.Node;
import rx.Observable;

/**
 * Interface for retrieving and aggregating nodes.
 * A NodeAggregator will generally call a set of remote sources to get
 * information which can be processed and converted into {@link Node}s.
 * <p>
 * Observables should be created and returned. Ideally services such as
 * {@link NettyServiceCaller} should be used to make use of for instance RxNetty
 * which returns an {@link Observable} from the start.
 * Alternatively, although not recommended, is to cast a normal Collection of
 * Nodes into an Observable using {@code return Observable.from(nodes);}
 * @author Andreas Evers
 */
public interface NodeAggregator {

    /**
     * Retrieves and aggregated nodes from one or more external sources.
     * @return an Observable of Nodes, preferably hot
     */
    Observable<Node> aggregateNodes();
}
