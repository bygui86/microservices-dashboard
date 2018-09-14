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

import org.apache.commons.lang3.builder.CompareToBuilder;

import java.io.Serializable;

/**
 * Simple node event when something goes wrong.
 *
 * @author Andreas Evers
 */
public class NodeEvent extends SystemEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String nodeId;

    public NodeEvent(String nodeId, String message) {
        super(message);
        this.nodeId = nodeId;
    }

    public NodeEvent(String nodeId, String message, Throwable throwable) {
        super(message, throwable);
        this.nodeId = nodeId;
    }

    public String getNodeId() {
        return nodeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        NodeEvent nodeEvent = (NodeEvent) o;

        return nodeId != null ? nodeId.equals(nodeEvent.nodeId) : nodeEvent.nodeId == null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (nodeId != null ? nodeId.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(Object o) {
        return CompareToBuilder.reflectionCompare(this, o);
    }

    @Override
    public String toString() {
        return "NodeEvent{" +
                "nodeId='" + nodeId + '\'' +
                ", message='" + getMessage() + '\'' +
                ", throwable=" + getThrowable() +
                "}";
    }
}
