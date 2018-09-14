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
package be.ordina.msdashboard.controllers;

import be.ordina.msdashboard.nodes.stores.EventStore;
import be.ordina.msdashboard.nodes.model.SystemEvent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collection;
import java.util.concurrent.ConcurrentSkipListSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link EventsController}
 *
 * @author Tim De Bruyn
 */
@RunWith(MockitoJUnitRunner.class)
public class EventsControllerTest {

    @InjectMocks
    private EventsController eventsController;
    
    @Mock
    private EventStore eventStore;
    
    @Test
    public void getAllNodes() {
    	ConcurrentSkipListSet<SystemEvent> events = new ConcurrentSkipListSet<>();
    	events.add(new SystemEvent("A system event occurred"));
        when(eventStore.getEvents()).thenReturn(events);

        Collection<SystemEvent> nodes = eventsController.getEvents();

        assertThat(nodes).isNotNull();
        assertThat(nodes.size()).isEqualTo(1);
    }

    @Test
    public void deleteAllNodes() {
    	eventsController.deleteEvents();
    	verify(eventStore).deleteEvents();
    }
}
