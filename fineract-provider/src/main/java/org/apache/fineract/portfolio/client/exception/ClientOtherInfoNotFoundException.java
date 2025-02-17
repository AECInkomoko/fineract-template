/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.portfolio.client.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

/**
 * A {@link RuntimeException} thrown when client other info resources are not found.
 */
public class ClientOtherInfoNotFoundException extends AbstractPlatformResourceNotFoundException {

    public ClientOtherInfoNotFoundException(final Long id) {
        super("error.msg.client.other.info.id.invalid", "Client Other Info with identifier " + id + " does not exist", id);
    }

    public ClientOtherInfoNotFoundException(final Long id, final Long clientId) {
        super("error.msg.client.other.info.clientId.invalid", "Client Other Info with clientId " + clientId + " does not exist", id,
                clientId);
    }
}
