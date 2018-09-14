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
package be.ordina.msdashboard.nodes.uriresolvers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.springframework.cloud.netflix.eureka.EurekaDiscoveryClient;

import com.netflix.appinfo.InstanceInfo;

/**
 * Tests for {@link EurekaUriResolver}
 *
 * @author Tim De Bruyn
 */
public class EurekaUriResolverTest {

	private EurekaUriResolver eurekaUriResolver = new EurekaUriResolver();
    @Test
    public void resolveHomePageUrl() {
    	EurekaDiscoveryClient.EurekaServiceInstance instance = mock(EurekaDiscoveryClient.EurekaServiceInstance.class);
    	InstanceInfo instanceInfo = mock(InstanceInfo.class);
    	when(instance.getInstanceInfo()).thenReturn(instanceInfo);
    	when(instanceInfo.getHomePageUrl()).thenReturn("http://homepage:1000");
    	assertThat(eurekaUriResolver.resolveHomePageUrl(instance)).isEqualTo("http://homepage:1000");
    }

    @Test
    public void resolveHealthCheckUrl() {
    	EurekaDiscoveryClient.EurekaServiceInstance instance = mock(EurekaDiscoveryClient.EurekaServiceInstance.class);
    	InstanceInfo instanceInfo = mock(InstanceInfo.class);
    	when(instance.getInstanceInfo()).thenReturn(instanceInfo);
    	when(instanceInfo.getHealthCheckUrl()).thenReturn("http://homepage:1000/health");
    	assertThat(eurekaUriResolver.resolveHealthCheckUrl(instance)).isEqualTo("http://homepage:1000/health");
    }
}
