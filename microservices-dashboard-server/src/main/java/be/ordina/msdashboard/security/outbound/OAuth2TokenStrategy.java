/*
 * Copyright 2012-2017 the original author or authors.
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

package be.ordina.msdashboard.security.outbound;

import io.netty.buffer.ByteBuf;
import io.reactivex.netty.protocol.http.client.HttpClientRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;

/**
 * Applies the inbound Spring security context to the outgoing aggregator requests
 *
 * @author Andreas Evers
 */
public class OAuth2TokenStrategy implements OutboundSecurityStrategy<Authentication> {

	private static final Logger logger = LoggerFactory.getLogger(OAuth2TokenStrategy.class);

	@Override
	public String getType() {
		return "forward-oauth2-token";
	}

	@Override
	public void apply(HttpClientRequest<ByteBuf> request, Authentication auth) {
		logger.info("OAuth2TokenStrategy called for auth: " + auth + " and request: " + request);
		if (auth != null) {
			if (auth instanceof OAuth2Authentication) {
				Object details = auth.getDetails();
				if (details instanceof OAuth2AuthenticationDetails) {
					logger.info("OAuth2 authentication details found");
					OAuth2AuthenticationDetails oauth = (OAuth2AuthenticationDetails) details;
					String accessToken = oauth.getTokenValue();
					String tokenType = oauth.getTokenType() == null ? "Bearer" : oauth.getTokenType();
					request.withHeader("Authorization", tokenType + " " + accessToken);
				} else {
					logger.info("No OAuth2 authentication details found");
				}
			}
		} else {
			logger.warn("OAuth2TokenStrategy enabled, but inbound request does not contain a Spring Security Authentication context");
		}
	}
}
