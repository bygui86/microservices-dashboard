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
package be.ordina.msdashboard.nodes.model;

import org.apache.commons.lang3.builder.CompareToBuilder;

import java.io.Serializable;
import java.util.Objects;

/**
 * Simple system event when something goes wrong.
 *
 * @author Andreas Evers
 */
public class SystemEvent implements Serializable, Comparable {

    private static final long serialVersionUID = 1L;

    private String message;
    private Throwable throwable;

    public SystemEvent(String message) {
        this.message = message;
    }

    public SystemEvent(String message, Throwable throwable) {
        this.message = message;
        this.throwable = throwable;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    @Override
    public boolean equals(Object o) {
        return Objects.deepEquals(this, o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, throwable);
    }

    @Override
    public int compareTo(Object o) {
        return CompareToBuilder.reflectionCompare(this, o);
    }

    @Override
    public String toString() {
        return "SystemEvent{" +
                "message='" + message + '\'' +
                ", throwable=" + throwable +
                '}';
    }
}
