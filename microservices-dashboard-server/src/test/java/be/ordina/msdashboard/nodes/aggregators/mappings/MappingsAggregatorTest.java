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

import be.ordina.msdashboard.nodes.aggregators.ErrorHandler;
import be.ordina.msdashboard.nodes.aggregators.NettyServiceCaller;
import be.ordina.msdashboard.nodes.model.Node;
import be.ordina.msdashboard.nodes.uriresolvers.UriResolver;
import be.ordina.msdashboard.security.config.DefaultStrategyBeanProvider;
import be.ordina.msdashboard.security.outbound.SecurityStrategyFactory;
import com.google.common.collect.Lists;
import io.reactivex.netty.protocol.http.client.HttpClientRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import rx.Observable;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;

import java.util.*;

import static be.ordina.msdashboard.nodes.aggregators.Constants.CONFIG_SERVER;
import static be.ordina.msdashboard.nodes.aggregators.Constants.DISCOVERY;
import static be.ordina.msdashboard.nodes.aggregators.Constants.HYSTRIX;
import static be.ordina.msdashboard.nodes.aggregators.health.HealthProperties.DISK_SPACE;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.startsWith;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

/**
 * Tests for {@link MappingsAggregator}
 *
 * @author Tim De Bruyn
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(MappingsToNodeConverter.class)
public class MappingsAggregatorTest {

    @InjectMocks
    private MappingsAggregator aggregator;

    @Mock
    private DiscoveryClient discoveryClient;
    @Mock
    private UriResolver uriResolver;
    @Mock
    private MappingsProperties properties;
    @Mock
    private NettyServiceCaller caller;
    @Mock
    private ErrorHandler errorHandler;
    @Mock
    private SecurityStrategyFactory securityStrategyFactory;
    @SuppressWarnings("rawtypes")
    @Captor
    private ArgumentCaptor<HttpClientRequest> requestCaptor;

    @Before
    public void before() {
        when(properties.getFilteredServices()).thenReturn(
                Lists.newArrayList(HYSTRIX, DISK_SPACE, DISCOVERY, CONFIG_SERVER));
        when(properties.getSecurity()).thenReturn(SecurityStrategyFactory.NONE);
        doReturn(new DefaultStrategyBeanProvider()).when(securityStrategyFactory).getStrategy(anyString());
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    public void shouldGetMappingNodesFromService() {
        when(properties.getRequestHeaders()).thenReturn(requestHeaders());
        Map retrievedMap = new HashMap();
        Observable retrievedMapObservable = Observable.just(retrievedMap);
        when(caller.retrieveJsonFromRequest(anyString(), any(HttpClientRequest.class)))
                .thenReturn(retrievedMapObservable);
        mockStatic(MappingsToNodeConverter.class);
        PowerMockito.when(MappingsToNodeConverter.convertToNodes(anyString(), anyMap()))
                .thenReturn(Observable.from(correctNodes()));

        TestSubscriber<Node> testSubscriber = new TestSubscriber<>();
        aggregator.getMappingNodesFromService("testService", "testUrl", null).toBlocking().subscribe(testSubscriber);
        List<Node> nodes = testSubscriber.getOnNextEvents();

        verify(caller, times(1)).retrieveJsonFromRequest(eq("testService"), requestCaptor.capture());
        assertThat(requestCaptor.getValue().getHeaders().entries()).usingElementComparator(stringEntryComparator())
                .containsExactlyElementsOf(requestHeaders().entrySet());
        assertThat(nodes).containsOnly(new Node("Node1"), new Node("Node2"));
    }

    @SuppressWarnings("unchecked")
    @Test(expected = RuntimeException.class)
    public void shouldFailEntireMappingNodeRetrievalChainOnGlobalRuntimeExceptions() {
        when(properties.getRequestHeaders()).thenReturn(requestHeaders());
        when(caller.retrieveJsonFromRequest(anyString(), any(HttpClientRequest.class)))
                .thenThrow(new RuntimeException());

        TestSubscriber<Node> testSubscriber = new TestSubscriber<>();
        aggregator.getMappingNodesFromService("testService", "testUrl", null).toBlocking().subscribe(testSubscriber);
        testSubscriber.getOnNextEvents();
        testSubscriber.assertCompleted();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    public void shouldReturnEmptyObservableOnEmptySourceObservable() {
        when(properties.getRequestHeaders()).thenReturn(requestHeaders());
        Observable retrievedMapObservable = Observable.empty();
        when(caller.retrieveJsonFromRequest(anyString(), any(HttpClientRequest.class)))
                .thenReturn(retrievedMapObservable);

        TestSubscriber<Node> testSubscriber = new TestSubscriber<>();
        aggregator.getMappingNodesFromService("testService", "testUrl", null).toBlocking().subscribe(testSubscriber);
        testSubscriber.assertNoValues();
        testSubscriber.assertCompleted();

        verify(caller, times(1)).retrieveJsonFromRequest(eq("testService"), requestCaptor.capture());
        assertThat(requestCaptor.getValue().getHeaders().entries()).usingElementComparator(stringEntryComparator())
                .containsExactlyElementsOf(requestHeaders().entrySet());
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    public void shouldReturnEmptyObservableOnErroneousConversion() {
        when(properties.getRequestHeaders()).thenReturn(requestHeaders());
        Map retrievedMap = new HashMap();
        Observable retrievedMapObservable = Observable.just(retrievedMap).publish().autoConnect();
        when(caller.retrieveJsonFromRequest(anyString(), any(HttpClientRequest.class)))
                .thenReturn(retrievedMapObservable);
        mockStatic(MappingsToNodeConverter.class);
        PowerMockito.when(MappingsToNodeConverter.convertToNodes(anyString(), anyMap()))
                .thenThrow(new RuntimeException("Error1"));

        TestSubscriber<Node> testSubscriber = new TestSubscriber<>();
        aggregator.getMappingNodesFromService("testService", "testUrl", null).toBlocking().subscribe(testSubscriber);
        testSubscriber.getOnNextEvents();
        testSubscriber.assertNoValues();
        testSubscriber.assertCompleted();

        verify(caller, times(1)).retrieveJsonFromRequest(eq("testService"), requestCaptor.capture());
        assertThat(requestCaptor.getValue().getHeaders().entries()).usingElementComparator(stringEntryComparator())
                .containsExactlyElementsOf(requestHeaders().entrySet());
    }

    @Test
    public void shouldGetServiceIdsFromDiscoveryClient() {
        when(discoveryClient.getServices()).thenReturn(asList("svc1", "SVC2", "zuul", "svc3"));

        TestSubscriber<String> testSubscriber = new TestSubscriber<>();
        aggregator.getServiceIdsFromDiscoveryClient().toBlocking().subscribe(testSubscriber);
        testSubscriber.assertValues("svc1", "svc2", "svc3");
        testSubscriber.assertNoErrors();
        testSubscriber.assertCompleted();
    }

    @Test(expected = RuntimeException.class)
    public void shouldFailEntireServiceDiscoveryChainOnGlobalRuntimeExceptions() {
        when(discoveryClient.getServices()).thenThrow(new RuntimeException());

        TestSubscriber<String> testSubscriber = new TestSubscriber<>();
        aggregator.getServiceIdsFromDiscoveryClient().toBlocking().subscribe(testSubscriber);
    }

    @Test
    public void shouldReturnValidServicesOnErroneousDiscovery() {
        when(discoveryClient.getServices()).thenReturn(asList("svc1", null, "zuul", "svc3"));

        TestSubscriber<String> testSubscriber = new TestSubscriber<>();
        aggregator.getServiceIdsFromDiscoveryClient().toBlocking().subscribe(testSubscriber);
        testSubscriber.assertValues("svc1", "svc3");
        testSubscriber.assertCompleted();

        verify(errorHandler, times(1)).handleSystemError(anyString(), any(Throwable.class));
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void shouldAggregateNodes() {
        aggregator = spy(new MappingsAggregator(discoveryClient, uriResolver, properties, caller, errorHandler, securityStrategyFactory));

        Observable observable = Observable.from(asList("svc1", null, "zuul", "svc3"));
        doReturn(observable).when(aggregator).getServiceIdsFromDiscoveryClient();
        when(discoveryClient.getInstances(anyString())).then(i -> {
            ServiceInstance serviceInstance = mock(ServiceInstance.class);
            when(serviceInstance.getServiceId()).thenReturn(i.getArgumentAt(0, String.class));
            return asList(serviceInstance);
        });
        when(uriResolver.resolveMappingsUrl(any(ServiceInstance.class))).then(i -> i.getArgumentAt(0, ServiceInstance.class).getServiceId());
        doAnswer(i -> Observable.from(asList(new Node(i.getArgumentAt(0, String.class))))).when(aggregator).getMappingNodesFromService(anyString(), anyString(), any());

        TestSubscriber<Node> testSubscriber = new TestSubscriber<>();
        aggregator.aggregateNodes().toBlocking().subscribe(testSubscriber);
        assertThat(testSubscriber.getOnNextEvents()).extracting("id").containsExactly("svc1", null, "zuul", "svc3");
        testSubscriber.assertCompleted();
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void shouldEmitErrorOnClassCastException() {
        aggregator = spy(new MappingsAggregator(discoveryClient, uriResolver, properties, caller, errorHandler, securityStrategyFactory));

        Observable observable = Observable.from(asList("svc1", null, "zuul", "svc3"));
        doReturn(observable).when(aggregator).getServiceIdsFromDiscoveryClient();
        when(discoveryClient.getInstances(anyString())).then(i -> {
            ServiceInstance serviceInstance = mock(ServiceInstance.class);
            when(serviceInstance.getServiceId()).thenReturn(i.getArgumentAt(0, String.class));
            return asList(serviceInstance);
        });
        when(uriResolver.resolveMappingsUrl(any(ServiceInstance.class))).then(i -> i.getArgumentAt(0, ServiceInstance.class).getServiceId());
        doAnswer(i -> Observable.from(asList(i.getArgumentAt(0, String.class)))).when(aggregator).getMappingNodesFromService(anyString(), anyString(), any()); // will give classcastexception

        TestSubscriber<Node> testSubscriber = new TestSubscriber<>();
        aggregator.aggregateNodes().toBlocking().subscribe(testSubscriber);
        testSubscriber.getOnNextEvents();
        testSubscriber.assertNoValues();
        testSubscriber.assertError(ClassCastException.class);
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void shouldAggregateAllValidNodesOnSingleServiceWithoutInstances() {
        aggregator = spy(new MappingsAggregator(discoveryClient, uriResolver, properties, caller, errorHandler, securityStrategyFactory));

        Observable observable = Observable.from(asList("svc1", "error", "svc3"))
                .subscribeOn(Schedulers.io()).publish().autoConnect();
        doReturn(observable).when(aggregator).getServiceIdsFromDiscoveryClient();
        when(discoveryClient.getInstances(startsWith("svc"))).then(i -> {
            ServiceInstance serviceInstance = mock(ServiceInstance.class);
            when(serviceInstance.getServiceId()).thenReturn(i.getArgumentAt(0, String.class));
            return asList(serviceInstance);
        });
        when(discoveryClient.getInstances(startsWith("error"))).thenReturn(new ArrayList<>());
        when(uriResolver.resolveMappingsUrl(any(ServiceInstance.class)))
                .then(i -> i.getArgumentAt(0, ServiceInstance.class).getServiceId());
        doAnswer(i -> Observable.from(asList(new Node(i.getArgumentAt(0, String.class)))))
                .when(aggregator).getMappingNodesFromService(anyString(), anyString(), any());

        TestSubscriber<Node> testSubscriber = new TestSubscriber<>();
        aggregator.aggregateNodes().toBlocking().subscribe(testSubscriber);
        List<Node> nodes = testSubscriber.getOnNextEvents();
        assertThat(nodes).extracting("id").containsExactly("svc1", "svc3");
        testSubscriber.assertNoErrors();
        verify(errorHandler, times(1)).handleSystemError(anyString(), any(Throwable.class));
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void shouldAggregateAllValidNodesOnNullInput() {
        aggregator = spy(new MappingsAggregator(discoveryClient, uriResolver, properties, caller, errorHandler, securityStrategyFactory));

        Observable observable = Observable.from(asList("svc1", null, "zuul", "svc3"))
                .subscribeOn(Schedulers.io()).publish().autoConnect();
        doReturn(observable).when(aggregator).getServiceIdsFromDiscoveryClient();
        when(discoveryClient.getInstances(startsWith("svc"))).then(i -> {
            ServiceInstance serviceInstance = mock(ServiceInstance.class);
            when(serviceInstance.getServiceId()).thenReturn(i.getArgumentAt(0, String.class));
            return asList(serviceInstance);
        });
        doThrow(new RuntimeException()).when(discoveryClient).getInstances(startsWith("zuul"));
        when(uriResolver.resolveMappingsUrl(any(ServiceInstance.class))).then(i -> i.getArgumentAt(0, ServiceInstance.class).getServiceId());
        doAnswer(i -> Observable.from(asList(new Node(i.getArgumentAt(0, String.class))))).when(aggregator).getMappingNodesFromService(anyString(), anyString(), any());

        TestSubscriber<Node> testSubscriber = new TestSubscriber<>();
        aggregator.aggregateNodes().toBlocking().subscribe(testSubscriber);
        List<Node> nodes = testSubscriber.getOnNextEvents();
        assertThat(nodes).extracting("id").containsExactly("svc1", "svc3");
        verify(errorHandler, times(2)).handleSystemError(anyString(), any(Throwable.class));
    }

    private Map<String, String> requestHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/hal+json");
        headers.put("Accept-Language", "en-us,en;q=0.5");
        return headers;
    }

    private Node[] correctNodes() {
        return new Node[]{new Node("Node1"), new Node(HYSTRIX), new Node(DISK_SPACE),
                new Node(DISCOVERY), new Node(CONFIG_SERVER), new Node("Node2")};
    }

    private Comparator<Map.Entry<String, String>> stringEntryComparator() {
        return (Comparator<Map.Entry<String, String>>) (o1, o2) ->
                (o1.getKey() + "|" + o1.getValue()).compareTo(o2.getKey() + "|" + o2.getValue());
    }
}
