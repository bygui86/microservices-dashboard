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
package be.ordina.msdashboard.nodes.aggregators.pact;


import static org.assertj.core.api.Assertions.assertThat;

import be.ordina.msdashboard.nodes.model.NodeTypes;
import org.junit.Test;

import be.ordina.msdashboard.nodes.model.Node;

/**
 * Tests for {@link PactToNodeConverter}
 *
 * @author Tim De Bruyn
 */
public class PactToNodeConverterTest {

	private PactToNodeConverter pactToNodeConverter = new PactToNodeConverter();
	
	@Test
	public void convert(){
		String source = "{\"provider\":{\"name\":\"providerNode\"},\"consumer\":{\"name\":\"consumerNode\"},\"interactions\":[{\"description\":"
				+ "\"A request to get the data of a customer\",\"request\":{\"method\":\"GET\",\"path\":"
				+ "\"/rel://pn:providerNode\",\"headers\":{\"globalid\":\"12345\",\"accept\":"
				+ "\"application/vnd.pxs.providerNode.v1+json;charset=UTF-8\"}},\"response\":"
				+ "{\"status\":200,\"headers\":{\"Content-Type\":\"application/vnd.pxs.providerNode.v1+json;charset=UTF-8\"},\"body\":"
				+ "{\"currentPoints\":264355,\"_embedded\":{\"pn:providerNode\":{\"providerNodeSpecificationId\":\"GLP_ID\"}}}}}],\"metadata\":"
				+ "{\"pact-specification\":{\"version\":\"3.0.0\"},\"pact-jvm\":{\"version\":\"2.3.3\"}},\"_links\":{\"self\":{\"title\":\"Pact\",\"name\":"
				+ "\"Pact between providerNode (v1.0.0) and consumerNode\",\"href\":"
				+ "\"http://someServer.be:7000/pacts/provider/providerNode/consumer/consumerNode/version/1.0.0\"},\"curies\":[{\"name\":\"pb\",\"href\":"
				+ "\"http://someotherserver.be:7000/doc/{rel}\",\"templated\":true}]}}";
		
		Node node = pactToNodeConverter.convert(source, "http://someServer.be:7000");
		assertThat(node.getId()).isEqualTo("consumerNode");
		assertThat(node.getLane()).isEqualTo(0);
		assertThat(node.getLinkedToNodeIds()).contains("pn:providerNode");
		assertThat(node.getDetails().get("url")).isEqualTo("http://someServer.be:7000");
		assertThat(node.getDetails().get("type")).isEqualTo(NodeTypes.UI_COMPONENT);
		assertThat(node.getDetails().get("status")).isEqualTo("UP");
	}
	
	@Test
	public void convertNoRel(){
		String source = "{\"provider\":{\"name\":\"providerNode\"},\"consumer\":{\"name\":\"consumerNode\"},\"interactions\":[{\"description\":"
				+ "\"A request to get the data of a customer\",\"request\":{\"method\":\"GET\",\"path\":"
				+ "\"http://someotherserver.be:7000/somepath/someobject\",\"headers\":{\"globalid\":\"12345\",\"accept\":"
				+ "\"application/vnd.pxs.providerNode.v1+json;charset=UTF-8\"}},\"response\":"
				+ "{\"status\":200,\"headers\":{\"Content-Type\":\"application/vnd.pxs.providerNode.v1+json;charset=UTF-8\"},\"body\":"
				+ "{\"currentPoints\":264355,\"_embedded\":{\"pn:providerNode\":{\"providerNodeSpecificationId\":\"GLP_ID\"}}}}}],\"metadata\":"
				+ "{\"pact-specification\":{\"version\":\"3.0.0\"},\"pact-jvm\":{\"version\":\"2.3.3\"}},\"_links\":{\"self\":{\"title\":\"Pact\",\"name\":"
				+ "\"Pact between providerNode (v1.0.0) and consumerNode\",\"href\":"
				+ "\"http://someServer.be:7000/pacts/provider/providerNode/consumer/consumerNode/version/1.0.0\"},\"curies\":[{\"name\":\"pb\",\"href\":"
				+ "\"http://someotherserver.be:7000/doc/{rel}\",\"templated\":true}]}}";
		
		Node node = pactToNodeConverter.convert(source, "http://someServer.be:7000");
		assertThat(node.getId()).isEqualTo("consumerNode");
		assertThat(node.getLane()).isEqualTo(0);
		assertThat(node.getLinkedToNodeIds()).contains("http://someotherserver.be:7000/somepath/someobject");
		assertThat(node.getDetails().get("url")).isEqualTo("http://someServer.be:7000");
		assertThat(node.getDetails().get("type")).isEqualTo(NodeTypes.UI_COMPONENT);
		assertThat(node.getDetails().get("status")).isEqualTo("UP");
	}
}
