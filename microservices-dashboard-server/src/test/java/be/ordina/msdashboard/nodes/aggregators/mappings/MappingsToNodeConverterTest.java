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
package be.ordina.msdashboard.nodes.aggregators.mappings;

import be.ordina.msdashboard.nodes.model.Node;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.junit.Test;
import rx.Observable;
import rx.observers.TestSubscriber;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link MappingsToNodeConverter}
 *
 * @author Andreas Evers
 */
public class MappingsToNodeConverterTest {

    private static final ObjectReader OBJECT_READER = new ObjectMapper().reader();

    @Test
    public void shouldConvertMostBasicNode() throws IOException {
        Map<String, Object> source = OBJECT_READER.forType(Map.class).readValue("{\"{[/home],methods=[GET]}\" : {" +
                "    \"bean\" : \"requestMappingHandlerMapping\", " +
                "    \"method\" : \"public java.lang.String be.ordina.controllers.HomeController.home()\"" +
                "  }}");

        Observable<Node> observable = MappingsToNodeConverter.convertToNodes("svc1", source);

        TestSubscriber<Node> testSubscriber = new TestSubscriber<>();
        observable.toBlocking().subscribe(testSubscriber);
        List<Node> nodes = testSubscriber.getOnNextEvents();
        assertThat(nodes).extracting("id").containsExactly("svc1","/home");
        assertThat(nodes).extracting("lane").containsExactly(2,1);
        assertThat(nodes).extracting("details").extracting("status").containsExactly("UP","UP");
        assertThat(nodes).extracting("details").extracting("type").containsExactly("MICROSERVICE","RESOURCE");
        assertThat(nodes).extracting("details").extracting("url").containsExactly(null,"/home");
        assertThat(nodes).extracting("details").extracting("methods").containsExactly(null,"GET");
    }

    @Test
    public void shouldMatchOnValidKeys() {
        String input = "{[/home],methods=[GET]}";
        assertThat(MappingsToNodeConverter.validMappingKey(input)).isTrue();
        input = "{methods=[GET]}";
        assertThat(MappingsToNodeConverter.validMappingKey(input)).isFalse();
        input = "{[/home]}";
        assertThat(MappingsToNodeConverter.validMappingKey(input)).isTrue();
        input = "{[]}";
        assertThat(MappingsToNodeConverter.validMappingKey(input)).isFalse();
        input = "{[/home],methods=[GET, PUT]}";
        assertThat(MappingsToNodeConverter.validMappingKey(input)).isTrue();
        input = "{[/home/],methods=[GET, PUT]}";
        assertThat(MappingsToNodeConverter.validMappingKey(input)).isTrue();
        input = "{[/root/home],methods=[GET, PUT]}";
        assertThat(MappingsToNodeConverter.validMappingKey(input)).isTrue();
        input = "/webjars/**";
        assertThat(MappingsToNodeConverter.validMappingKey(input)).isFalse();
        input = "_links";
        assertThat(MappingsToNodeConverter.validMappingKey(input)).isFalse();
    }

    @Test
    public void shouldExtractValidUrls() {
        String input = "{[/home],methods=[GET]}";
        assertThat(MappingsToNodeConverter.extractUrl(input)).isEqualTo("/home");
        input = "{[/home]}";
        assertThat(MappingsToNodeConverter.extractUrl(input)).isEqualTo("/home");
        input = "{[/home],methods=[GET, PUT]}";
        assertThat(MappingsToNodeConverter.extractUrl(input)).isEqualTo("/home");
        input = "{[/home/],methods=[GET, PUT]}";
        assertThat(MappingsToNodeConverter.extractUrl(input)).isEqualTo("/home/");
        input = "{[/root/home],methods=[GET, PUT]}";
        assertThat(MappingsToNodeConverter.extractUrl(input)).isEqualTo("/root/home");
        input = "{[/root/home/],methods=[GET, PUT]}";
        assertThat(MappingsToNodeConverter.extractUrl(input)).isEqualTo("/root/home/");
        input = "{produces=[application/json],[/root/home/],methods=[GET, PUT]}";
        assertThat(MappingsToNodeConverter.extractUrl(input)).isEqualTo("/root/home/");
    }

    @Test(expected = IllegalStateException.class)
    public void shouldFailOnEmptyUrl() {
        String input = "{[]}";
        MappingsToNodeConverter.extractUrl(input);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldFailOnMissingUrl() {
        String input = "{methods=[GET]}";
        MappingsToNodeConverter.extractUrl(input);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldFailOnMalformedUrl() {
        String input = "/webjars/**";
        MappingsToNodeConverter.extractUrl(input);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldFailOnBogusUrl() {
        String input = "_links";
        MappingsToNodeConverter.extractUrl(input);
    }

    @Test
    public void shouldExtractValidMethods() {
        String input = "{[/home],methods=[GET]}";
        assertThat(MappingsToNodeConverter.extractMethods(input).get()).isEqualTo("GET");
        input = "{[/home]}";
        assertThat(MappingsToNodeConverter.extractMethods(input).isPresent()).isFalse();
        input = "{[/home],methods=[GET, PUT]}";
        assertThat(MappingsToNodeConverter.extractMethods(input).get()).isEqualTo("GET, PUT");
        input = "{[/home],methods=[GET,PUT]}";
        assertThat(MappingsToNodeConverter.extractMethods(input).get()).isEqualTo("GET,PUT");
        input = "{[/home/],methods=[GET, PUT], produces=[application/json]}";
        assertThat(MappingsToNodeConverter.extractMethods(input).get()).isEqualTo("GET, PUT");
        input = "{[/home/root],methods=[GET, PUT],produces=[application/json]}";
        assertThat(MappingsToNodeConverter.extractMethods(input).get()).isEqualTo("GET, PUT");
    }

    @Test
    public void shouldNotMatchOnSpringKeys() {
        Map<String, String> map = new HashMap<>();
        String input = "public java.lang.Object org.springframework.boot.actuate.endpoint.mvc.EndpointMvcAdapter.invoke()";
        map.put("method", input);
        assertThat(MappingsToNodeConverter.isNonSpringMapping(map)).isFalse();
        input = "public java.lang.String be.ordina.controllers.TestController.home(be.springframework.dto.Test1,be.ordina.dto.Test2,org.springframework.ui.Model)";
        map.put("method", input);
        assertThat(MappingsToNodeConverter.isNonSpringMapping(map)).isTrue();
        input = "public java.lang.String be.springframework.controllers.TestController.home(java.util.Locale,org.springframework.ui.Model)";
        map.put("method", input);
        assertThat(MappingsToNodeConverter.isNonSpringMapping(map)).isTrue();
        input = "protected void be.springframework.controllers.TestController.home(java.util.Locale,org.springframework.ui.Model)";
        map.put("method", input);
        assertThat(MappingsToNodeConverter.isNonSpringMapping(map)).isTrue();
        input = "wrongformat";
        map.put("method", input);
        assertThat(MappingsToNodeConverter.isNonSpringMapping(map)).isTrue();
        input = "";
        map.put("method", input);
        assertThat(MappingsToNodeConverter.isNonSpringMapping(map)).isTrue();
    }

    @Test
    public void shouldReturnOnlyTopLevelNodeWhenNoneFound() throws IOException {
        Map<String, Object> source = OBJECT_READER.forType(Map.class).readValue("{}");

        Observable<Node> observable = MappingsToNodeConverter.convertToNodes("svc1", source);

        TestSubscriber<Node> testSubscriber = new TestSubscriber<>();
        observable.toBlocking().subscribe(testSubscriber);
        List<Node> onNextEvents = testSubscriber.getOnNextEvents();
        assertThat(onNextEvents).extracting("id").containsExactly("svc1");
        testSubscriber.assertCompleted();
    }

    @Test
    public void shouldHandleSmallestPossibleMapping() throws IOException {
        Map<String, Object> source = OBJECT_READER.forType(Map.class)
                .readValue("{\"{[/]}\" : {}}");

        Observable<Node> observable = MappingsToNodeConverter.convertToNodes("svc1", source);

        TestSubscriber<Node> testSubscriber = new TestSubscriber<>();
        observable.toBlocking().subscribe(testSubscriber);
        List<Node> nodes = testSubscriber.getOnNextEvents();
        assertThat(nodes).extracting("id").containsExactly("svc1","/");
        assertThat(nodes).extracting("details").extracting("status").containsExactly("UP","UP");
        assertThat(nodes).extracting("details").extracting("type").containsExactly("MICROSERVICE","RESOURCE");
    }

    @Test
    public void shouldIgnoreCommonNodes() throws IOException {
        Map<String, Object> source = OBJECT_READER.forType(Map.class)
                .readValue("{\"_links\" : {" +
                        "    \"self\" : {" +
                        "      \"href\" : \"http://localhost:8080/mappings\"" +
                        "    }" +
                        "  }, \"/**\" : {" +
                        "    \"bean\" : \"resourceHandlerMapping\"" +
                        "  }, \"/webjars/**\" : {" +
                        "    \"bean\" : \"resourceHandlerMapping\"" +
                        "  }, \"{[/home],methods=[GET]}\" : {" +
                        "    \"bean\" : \"requestMappingHandlerMapping\", " +
                        "    \"method\" : \"public java.lang.String be.ordina.controllers.HomeController.home()\"" +
                        "  }, \"{[/mappings || /mappings.json],methods=[GET],produces=[application/json]}\" : {" +
                        "    \"bean\" : \"endpointHandlerMapping\"," +
                        "    \"method\" : \"public java.lang.Object org.springframework.boot.actuate.endpoint.mvc.EndpointMvcAdapter.invoke()\"" +
                        "}}");

        Observable<Node> observable = MappingsToNodeConverter.convertToNodes("svc1", source);

        TestSubscriber<Node> testSubscriber = new TestSubscriber<>();
        observable.toBlocking().subscribe(testSubscriber);
        List<Node> nodes = testSubscriber.getOnNextEvents();
        assertThat(nodes).extracting("id").containsExactly("svc1","/home");
        assertThat(nodes).extracting("details").extracting("status").containsExactly("UP","UP");
        assertThat(nodes).extracting("details").extracting("type").containsExactly("MICROSERVICE","RESOURCE");
    }

    @Test
    public void shouldReturnOnlyTopLevelNodeWhenOnlyCommonNodes() throws IOException {
        Map<String, Object> source = OBJECT_READER.forType(Map.class)
                .readValue("{\"_links\" : {" +
                "    \"self\" : {" +
                "      \"href\" : \"http://localhost:8080/mappings\"" +
                "    }" +
                "  }, \"/**\" : {" +
                "    \"bean\" : \"resourceHandlerMapping\"" +
                "  }, \"/webjars/**\" : {" +
                "    \"bean\" : \"resourceHandlerMapping\"" +
                "  }, \"{[/mappings || /mappings.json],methods=[GET],produces=[application/json]}\" : {" +
                "    \"bean\" : \"endpointHandlerMapping\"," +
                "    \"method\" : \"public java.lang.Object org.springframework.boot.actuate.endpoint.mvc.EndpointMvcAdapter.invoke()\"" +
                "}}");

        Observable<Node> observable = MappingsToNodeConverter.convertToNodes("svc1", source);

        TestSubscriber<Node> testSubscriber = new TestSubscriber<>();
        observable.toBlocking().subscribe(testSubscriber);
        List<Node> nodes = testSubscriber.getOnNextEvents();
        assertThat(nodes).extracting("id").containsExactly("svc1");
        assertThat(nodes).extracting("details").extracting("status").containsExactly("UP");
        assertThat(nodes).extracting("details").extracting("type").containsExactly("MICROSERVICE");
        testSubscriber.assertCompleted();
    }
    
    @Test
    public void shouldIgnoreMethodMissing() throws IOException {
        Map<String, Object> source = OBJECT_READER.forType(Map.class).readValue("{\"{[/home],methods=[GET]}\" : {" +
                "    \"bean\" : \"requestMappingHandlerMapping\", " +
                "    \"nomethod\" : \"public java.lang.String be.ordina.controllers.HomeController.home()\"" +
                "  }}");

        Observable<Node> observable = MappingsToNodeConverter.convertToNodes("svc1", source);

        TestSubscriber<Node> testSubscriber = new TestSubscriber<>();
        observable.toBlocking().subscribe(testSubscriber);
        List<Node> nodes = testSubscriber.getOnNextEvents();
        assertThat(nodes).extracting("id").containsExactly("svc1", "/home");
        assertThat(nodes).extracting("details").extracting("status").containsExactly("UP", "UP");
        assertThat(nodes).extracting("details").extracting("type").containsExactly("MICROSERVICE", "RESOURCE");
        testSubscriber.assertCompleted();
    }
}
