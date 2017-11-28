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
package com.github.ketal.webservice.authorization;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;

import com.github.ketal.webservice.configuration.BaseWebserviceConfig;
import com.github.ketal.webservice.configuration.injection.Config;

@AdminRole
public class AdminRoleFilter implements ContainerRequestFilter {

    @Config 
    private BaseWebserviceConfig config;
    
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        if(config.getAdminResourceRole() != null && !config.getAdminResourceRole().isEmpty()) {
            if (!requestContext.getSecurityContext().isUserInRole(config.getAdminResourceRole())) {
                requestContext.abortWith(Response.status(Response.Status.FORBIDDEN).entity("Admin role is required.").build());
            }
        }
    }

}
