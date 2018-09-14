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
package be.ordina.msdashboard.nodes.stores;

import be.ordina.msdashboard.nodes.model.SystemEvent;

import java.util.concurrent.ConcurrentSkipListSet;

/**
 * Store for {@link SystemEvent}s, populated by an
 * {@link org.springframework.context.event.EventListener} emitting events.
 *
 * @author Andreas Evers
 */
public class EventStore {

    private ConcurrentSkipListSet<SystemEvent> events = new ConcurrentSkipListSet<>();

    @org.springframework.context.event.EventListener
    public void handleContextRefresh(SystemEvent event) {
        events.add(event);
    }

    public ConcurrentSkipListSet<SystemEvent> getEvents() {
        return events;
    }

    public void deleteEvents() {
        events.clear();
    }
}
