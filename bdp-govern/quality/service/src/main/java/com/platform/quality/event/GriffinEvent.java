/*
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
*/

package com.platform.quality.event;

/**
 * A semantic event which indicates that a griffin-defined action occurred.
 * This high-level event is generated by an action (such as an
 * <code>addJob</code>) when the task-specific action occurs.
 * The event is passed to every <code>GriffinHook</code> object
 * that registered to receive such events using configuration.
 *
 * @author Eugene Liu
 * @since 0.3
 */
public interface GriffinEvent<T> {
    /**
     * @return concrete event type
     */
    EventType getType();

    /**
     * @return concrete event pointcut type
     */
    EventPointcutType getPointcut();

    /**
     * @return concrete event source type
     */
    EventSourceType getSourceType();

    /**
     * The object on which the Event initially occurred.
     *
     * @return The object on which the Event initially occurred.
     */
    T getSource();
}