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

import org.springframework.cloud.client.ServiceInstance;

/**
 * Resolves urls from a {@link ServiceInstance}.
 *
 * @author Andreas Evers
 */
public interface UriResolver {

    /**
     * Resolves the homepage url of the given instance
     * @param instance the instance for which the url has to be resolved
     * @return the homepage url
     */
    String resolveHomePageUrl(ServiceInstance instance);

    /**
     * Resolves the health check url of the given instance
     * @param instance the instance for which the url has to be resolved
     * @return the health check url
     */
    String resolveHealthCheckUrl(ServiceInstance instance);

    /**
     * Resolves the mappings url of the given instance
     * @param instance the instance for which the url has to be resolved
     * @return the mappings url
     */
    String resolveMappingsUrl(ServiceInstance instance);

}
