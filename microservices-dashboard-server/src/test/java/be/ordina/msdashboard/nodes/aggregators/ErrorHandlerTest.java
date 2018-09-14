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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.context.ApplicationEventPublisher;

import be.ordina.msdashboard.nodes.model.NodeEvent;
import be.ordina.msdashboard.nodes.model.SystemEvent;

/**
 * Tests for {@link ErrorHandler}
 *
 * @author Tim De Bruyn
 */
public class ErrorHandlerTest {

	private ErrorHandler errorHandler;
	
	@Mock
	private ApplicationEventPublisher publisher;
	
	@Before
	public void before(){
		publisher = Mockito.mock(ApplicationEventPublisher.class);
		errorHandler = new ErrorHandler(publisher);
	}
	
	@Test
	public void nodeWarning(){
		ArgumentCaptor<NodeEvent> captor = ArgumentCaptor.forClass(NodeEvent.class);
		errorHandler.handleNodeWarning("serviceId", "some message");
		
		verify(publisher).publishEvent(captor.capture());
		NodeEvent nodeEvent = captor.getValue();
		assertThat(nodeEvent.getNodeId()).isEqualTo("serviceId");
		assertThat(nodeEvent.getMessage()).isEqualTo("some message");
	}

	@Test
    public void nodeError() {
		ArgumentCaptor<NodeEvent> captor = ArgumentCaptor.forClass(NodeEvent.class);
		
		Throwable t = new Throwable();
		errorHandler.handleNodeError("serviceId", "some message", t);
		
		verify(publisher).publishEvent(captor.capture());
		NodeEvent nodeEvent = captor.getValue();
		assertThat(nodeEvent.getNodeId()).isEqualTo("serviceId");
		assertThat(nodeEvent.getMessage()).isEqualTo("some message");
		assertThat(nodeEvent.getThrowable()).isEqualTo(t);
    }

	@Test
    public void systemError() {
		ArgumentCaptor<SystemEvent> captor = ArgumentCaptor.forClass(SystemEvent.class);
		
		Throwable t = new Throwable();
		errorHandler.handleSystemError("some message", t);
		
		verify(publisher).publishEvent(captor.capture());
		SystemEvent nodeEvent = captor.getValue();
		assertThat(nodeEvent.getThrowable()).isEqualTo(t);
		assertThat(nodeEvent.getMessage()).isEqualTo("some message");
    }
}
