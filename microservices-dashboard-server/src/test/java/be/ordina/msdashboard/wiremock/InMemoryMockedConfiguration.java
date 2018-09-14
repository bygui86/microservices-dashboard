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

package be.ordina.msdashboard.wiremock;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Separate wiremocks with separate ports are required to keep parallel integration tests from conflicting
 */
@Configuration
public class InMemoryMockedConfiguration {

	@Bean
	@ConditionalOnProperty(value = "pact-broker.url", havingValue = "https://localhost:8089")
	protected InMemoryDefaultWireMock inMemoryWireMock() {

		return new InMemoryDefaultWireMock();
	}

	@Bean
	// @Conditional(ForwardInboundAuthorizationHeaderStrategyConfiguration.Condition.class)
	@ConditionalOnProperty({
			"msdashboard.security.strategies.forward-inbound-auth-header",
			"msdashboard.security.strategies.forward-inbound-auth-header[0]"
	})
	protected InMemoryForwardInboundAuthHeaderWireMock inMemoryForwardInboundAuthHeaderWireMock() {

		return new InMemoryForwardInboundAuthHeaderWireMock();
	}

	@Bean
	// @Conditional(ForwardOAuth2TokenStrategyConfiguration.Condition.class)
	@ConditionalOnProperty({
			"msdashboard.security.strategies.forward-oauth2-token",
			"msdashboard.security.strategies.forward-oauth2-token[0]"
	})
	protected InMemoryForwardOAuth2TokenWireMock inMemoryForwardOAuth2TokenWireMock() {

		return new InMemoryForwardOAuth2TokenWireMock();
	}

}
