/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.ketal.cornerstone.webservice.exception.mapper;

import java.util.UUID;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.ext.Provider;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.ketal.cornerstone.webservice.model.WsError;

/*
 * To catch all non mapped and unexpected exceptions.
 */
@Provider
public class WsThrowableMapper extends AbstractExceptionMapper<Throwable> {

    private static final Logger logger = LogManager.getLogger(WsThrowableMapper.class);

    @Override
    protected ResponseBuilder getResponseBuilder() {
        return Response.serverError();
    }

    @Override
    protected WsError getWsError() {
        WsError error = new WsError("Error processing your request. Please try again or contact your administrator.", UUID.randomUUID().toString());

        logger.warn("Exception caught by WsThrowableMapper. ReferenceNumber: " + error.getReferenceId(), this.exception);
        return error;
    }
}
