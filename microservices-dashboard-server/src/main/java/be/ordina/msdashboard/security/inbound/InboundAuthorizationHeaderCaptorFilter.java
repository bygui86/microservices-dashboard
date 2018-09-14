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

package be.ordina.msdashboard.security.inbound;

import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Filter to capture the authentication header
 *
 * @author Kevin van Houtte
 * @author Andreas Evers
 */
public class InboundAuthorizationHeaderCaptorFilter extends GenericFilterBean {

	@Override
	public void doFilter(final ServletRequest req,
						 final ServletResponse res,
						 final FilterChain chain) throws IOException, ServletException {
		final HttpServletRequest request = (HttpServletRequest) req;
		String authHeader = request.getHeader("Authorization");
		if (authHeader != null) {
			AuthorizationHeaderHolder.set(authHeader);
		}
		chain.doFilter(req, res);
	}
}
