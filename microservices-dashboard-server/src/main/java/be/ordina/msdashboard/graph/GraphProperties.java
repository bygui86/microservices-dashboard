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

import java.util.Arrays;
import java.util.List;

import static be.ordina.msdashboard.nodes.model.NodeTypes.MICROSERVICE;
import static be.ordina.msdashboard.nodes.model.NodeTypes.RESOURCE;

/**
 * Properties for the graph response.
 *
 * @author Andreas Evers
 */
public class GraphProperties {

    // TODO: Implement this field in the aggregators
    public static final String DESCRIPTION = "description";
    public static final String DB = "DB";
    public static final String REST = "REST";
    public static final String SOAP = "SOAP";
    public static final String JMS = "JMS";

    private String ui = "UI Components";
    private String resources = "Resources";
    private String microservices = "Microservices";
    private String backends = "Backends";

    private List<String> types = Arrays.asList(DB, MICROSERVICE, REST, SOAP, JMS, RESOURCE);

    public String getUi() {
        return ui;
    }

    public void setUi(String ui) {
        this.ui = ui;
    }

    public String getResources() {
        return resources;
    }

    public void setResources(String resources) {
        this.resources = resources;
    }

    public String getMicroservices() {
        return microservices;
    }

    public void setMicroservices(String microservices) {
        this.microservices = microservices;
    }

    public String getBackends() {
        return backends;
    }

    public void setBackends(String backends) {
        this.backends = backends;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }
}
