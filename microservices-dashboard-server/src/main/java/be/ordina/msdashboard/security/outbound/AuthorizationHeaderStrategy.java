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

/**
 * Applies the inbound authorization header to the outgoing aggregator requests
 *
 * @author Kevin van Houtte
 */
public class AuthorizationHeaderStrategy implements OutboundSecurityStrategy<String> {

	private static final Logger logger = LoggerFactory.getLogger(AuthorizationHeaderStrategy.class);

	@Override
	public String getType() {
		return "forward-inbound-auth-header";
	}

	@Override
	public void apply(HttpClientRequest<ByteBuf> request, String authorizationHeader) {
		if (authorizationHeader != null) {
			request.withHeader("Authorization", authorizationHeader);
		} else {
			logger.warn("AuthorizationHeaderStrategy enabled, but inbound request does not contain an authorization header");
		}
	}
}
