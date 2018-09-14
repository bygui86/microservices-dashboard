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
package be.ordina.msdashboard.nodes.aggregators;

import io.netty.buffer.ByteBuf;
import io.reactivex.netty.client.RxClient;
import io.reactivex.netty.protocol.http.AbstractHttpContentHolder;
import io.reactivex.netty.protocol.http.client.CompositeHttpClient;
import io.reactivex.netty.protocol.http.client.CompositeHttpClientBuilder;
import io.reactivex.netty.protocol.http.client.HttpClientRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.json.JacksonJsonParser;
import rx.Observable;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Map;

import static io.reactivex.netty.client.MaxConnectionsBasedStrategy.DEFAULT_MAX_CONNECTIONS;
import static java.text.MessageFormat.format;

/**
 * Convenience class for retrieving JSON using
 * <a href="https://github.com/ReactiveX/RxNetty">RxNetty</a>.
 *
 * @author Andreas Evers
 */
public class NettyServiceCaller {

	private static final Logger logger = LoggerFactory.getLogger(NettyServiceCaller.class);

	private final CompositeHttpClient<ByteBuf, ByteBuf> rxClient;
	private final ErrorHandler errorHandler;

	@Deprecated
	public NettyServiceCaller(ErrorHandler errorHandler) {
		this.errorHandler = errorHandler;
		this.rxClient = new CompositeHttpClientBuilder<ByteBuf, ByteBuf>().withMaxConnections(DEFAULT_MAX_CONNECTIONS).build();
	}

	public NettyServiceCaller(ErrorHandler errorHandler, CompositeHttpClient<ByteBuf, ByteBuf> rxClient) {
		this.errorHandler = errorHandler;
		this.rxClient = rxClient;
	}

	/**
	 * Calls the remote service using the provided request, applies error handling and
	 * converts the response into a {@link Map}. The entire request and response are
	 * executed and handled in a hot observable.
	 *
	 * @param serviceId the id of the service for which the request is made
	 * @param request   the request which has to be executed using RxNetty
	 * @return an {@link Observable} emitting the JSON response as a Map with String keys
	 * and Object values.
	 */
	public Observable<Map<String, Object>> retrieveJsonFromRequest(String serviceId, HttpClientRequest<ByteBuf> request) {
		RxClient.ServerInfo serverInfo = getServerInfoFromRequestOrClient(request, rxClient);

		return rxClient.submit(serverInfo, request)
				.publish().autoConnect()
				.doOnError(el -> errorHandler.handleNodeError(serviceId, format("Error retrieving node(s) for url {0} with headers {1}: {2}",
						request.getUri(), request.getHeaders().entries(), el), el))
				.filter(r -> {
					if (r.getStatus().code() < 400) {
						return true;
					} else {
						errorHandler.handleNodeWarning(serviceId, "Exception " + r.getStatus() + " for url " + request.getUri() + " with headers " + r.getHeaders().entries());
						return false;
					}
				})
				.flatMap(AbstractHttpContentHolder::getContent)
				.map(data -> data.toString(Charset.defaultCharset()))
				.map(response -> {
					JacksonJsonParser jsonParser = new JacksonJsonParser();
					return jsonParser.parseMap(response);
				})
				.doOnNext(r -> logger.info("Json retrieved from call: {}", r))
				.onErrorResumeNext(Observable.empty());
	}

	public static RxClient.ServerInfo getServerInfoFromRequestOrClient(HttpClientRequest<ByteBuf> request,
																	   CompositeHttpClient<ByteBuf, ByteBuf> rxClient) {
		RxClient.ServerInfo serverInfo = rxClient.getDefaultServer();

		try {
			URI uri = new URI(request.getUri());

			final String host = uri.getHost();
			if (null != host) {
				int port = uri.getPort();
				if (port < 0) {
					String scheme = uri.getScheme();
					if (null != scheme) {
						if ("http".equals(scheme)) {
							port = 80;
						} else if ("https".equals(scheme)) {
							port = 443;
						}
					}
				}
				serverInfo = new RxClient.ServerInfo(host, port);
			}
		} catch (URISyntaxException e) {
			logger.error("Could not extract server info from request: {0}", e.getMessage());
		}

		return serverInfo;
	}
}
