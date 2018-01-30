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
package com.github.ketal.cornerstone.webservice.authorization;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;

import com.github.ketal.cornerstone.webservice.configuration.BaseWebserviceConfig;
import com.github.ketal.cornerstone.webservice.configuration.injection.Config;

@MonitorRole
public class MonitorRoleFilter implements ContainerRequestFilter {

    @Config
    private BaseWebserviceConfig config;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String monitorRole = config.getMonitorResourceRole();
        String adminRole = config.getAdminResourceRole();

        if(adminRole == null || adminRole.isEmpty() || monitorRole == null || monitorRole.isEmpty()) {
            return;
        }
        
        if(adminRole != null && !adminRole.isEmpty() && requestContext.getSecurityContext().isUserInRole(adminRole)) {
            return;
        }
        
        if(monitorRole != null && !monitorRole.isEmpty() && requestContext.getSecurityContext().isUserInRole(monitorRole)) {
            return;
        }
        
        requestContext.abortWith(Response.status(Response.Status.FORBIDDEN).entity("User does not have required role.").build());
    }
}
