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

package be.ordina.msdashboard.security.config.strategies;

import be.ordina.msdashboard.security.config.StrategyBeanProvider;
import be.ordina.msdashboard.security.outbound.OAuth2TokenStrategy;
import be.ordina.msdashboard.security.outbound.OutboundSecurityObjectProvider;
import be.ordina.msdashboard.security.outbound.OutboundSecurityStrategy;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Security strategy which uses an existing OAuth2 SSO security context. The bearer token stored in the context is
 * passed to the outgoing aggregator requests as Authorization header.
 * <br>
 * The Conditional expression is used to support both yaml-style properties and property-style properties.
 * Cfr. https://github.com/spring-projects/spring-boot/issues/7483 (currently unsolved)
 *
 * @author Andreas Evers
 */
// @Conditional(ForwardOAuth2TokenStrategyConfiguration.Condition.class)
@ConditionalOnProperty({
		"msdashboard.security.strategies.forward-oauth2-token",
		"msdashboard.security.strategies.forward-oauth2-token[0]"
})
@Configuration
public class ForwardOAuth2TokenStrategyConfiguration {

	// public static class Condition extends SpringBootCondition {
	//
	// 	@Autowired
	// 	private Environment environment;
	//
	// 	@Override
	// 	public ConditionOutcome getMatchOutcome(final ConditionContext context,
	// 	                                        final AnnotatedTypeMetadata metadata) {
	//
	// 		// final RelaxedPropertyResolver resolver = new RelaxedPropertyResolver(context.getEnvironment());
	// 		// final String propertiesProperty = resolver.getProperty("msdashboard.security.strategies.forward-oauth2-token", String.class);
	// 		// final String yamlProperty = resolver.getProperty("msdashboard.security.strategies.forward-oauth2-token[0]", String.class);
	//
	// 		final String propertiesProperty = environment.getProperty("msdashboard.security.strategies.forward-oauth2-token", String.class);
	// 		final String yamlProperty = environment.getProperty("msdashboard.security.strategies.forward-oauth2-token[0]", String.class);
	//
	// 		return new ConditionOutcome(propertiesProperty != null || yamlProperty != null, "Conditional on forward-oauth2-token value");
	// 	}
	// }

	@ConditionalOnMissingBean
	@Bean
	public OutboundSecurityObjectProvider outboundSecurityObjectProvider() {

		return new OutboundSecurityObjectProvider();
	}

	@ConditionalOnMissingBean
	@Bean
	public OAuth2TokenStrategy oAuth2TokenStrategy() {

		return new OAuth2TokenStrategy();
	}

	@ConditionalOnMissingBean
	@Bean
	public StrategyBeanProvider forwardOAuth2TokenStrategyBeanProvider(
			final OutboundSecurityObjectProvider outboundSecurityObjectProvider,
			final OAuth2TokenStrategy oAuth2TokenStrategy) {

		return new ForwardOAuth2TokenStrategyBeanProvider(outboundSecurityObjectProvider, oAuth2TokenStrategy);
	}

	public class ForwardOAuth2TokenStrategyBeanProvider implements StrategyBeanProvider {

		private final OutboundSecurityObjectProvider outboundSecurityObjectProvider;

		private final OutboundSecurityStrategy oAuth2TokenStrategy;

		@Override
		public String getType() {

			return "forward-oauth2-token";
		}

		public ForwardOAuth2TokenStrategyBeanProvider(final OutboundSecurityObjectProvider outboundSecurityObjectProvider,
		                                              final OAuth2TokenStrategy oAuth2TokenStrategy) {

			this.outboundSecurityObjectProvider = outboundSecurityObjectProvider;
			this.oAuth2TokenStrategy = oAuth2TokenStrategy;
		}

		@Override
		public OutboundSecurityObjectProvider getOutboundSecurityObjectProvider() {

			return outboundSecurityObjectProvider;
		}

		@Override
		public OutboundSecurityStrategy getOutboundSecurityStrategy() {

			return oAuth2TokenStrategy;
		}
	}
}


