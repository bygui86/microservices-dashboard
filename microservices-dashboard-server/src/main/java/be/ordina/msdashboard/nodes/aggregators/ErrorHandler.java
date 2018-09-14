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

import be.ordina.msdashboard.nodes.model.NodeEvent;
import be.ordina.msdashboard.nodes.model.SystemEvent;
import be.ordina.msdashboard.nodes.stores.EventStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import rx.Observable;

/**
 * Handler of errors emitted by {@link Observable}s.
 * <p>Its function is twofold:
 * <ol>
 *     <li>logging the error message and optional throwable,</li>
 *     <li>publishing the error as an event.</li>
 * </ol>
 * Event listeners can then pick up the published events for
 * instance how {@link EventStore} populates its store.
 *
 * @author Andreas Evers
 */
public class ErrorHandler {

    private static final Logger logger = LoggerFactory.getLogger(ErrorHandler.class);

    private ApplicationEventPublisher publisher;

    /**
     * Creates a new {@link ErrorHandler} that will handle passed in errors.
     * @param publisher an {@link ApplicationEventPublisher} capable of
     *                  publishing error events
     */
    public ErrorHandler(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    /**
     * Handles a node warning. Will log the message as a warning and publish
     * a new {@link NodeEvent} using the serviceId and message of the error.
     * @param serviceId id of the service the warning occurred for
     * @param message the message of the warning
     */
    public void handleNodeWarning(String serviceId, String message) {
        logger.warn(message);
        publisher.publishEvent(new NodeEvent(serviceId, message));
    }

    /**
     * Handles a node error. Will log the message and the throwable as an
     * error and publish a new {@link NodeEvent} using the serviceId,
     * message and throwable of the error.
     * @param serviceId id of the service the error occurred for
     * @param message the message of the error
     * @param el the throwable itself
     */
    public void handleNodeError(String serviceId, String message, Throwable el) {
        logger.error(message, el);
        publisher.publishEvent(new NodeEvent(serviceId, message, el));
    }

    /**
     * Handles a system error. Will log the message and the throwable as an
     * error and publish a new {@link SystemEvent} using the message and
     * throwable of the error.
     * <p>Use {@link #handleNodeError(String, String, Throwable)} instead when
     * serviceId is known.
     * @param message the message of the error
     * @param el the throwable itself
     */
    public void handleSystemError(String message, Throwable el) {
        logger.error(message, el);
        publisher.publishEvent(new SystemEvent(message, el));
    }
}
