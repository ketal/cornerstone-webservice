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
package com.github.ketal.cornerstone.webservice;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

import com.github.ketal.cornerstone.webservice.configuration.BaseWebserviceConfig;
import com.github.ketal.cornerstone.webservice.configuration.injection.Config;

@Provider
public class CORSFilter implements ContainerResponseFilter {

    @Config
    private BaseWebserviceConfig config;
    
    @Override
    public void filter(ContainerRequestContext request, ContainerResponseContext response) throws IOException {
        MultivaluedMap<String, Object> headers = response.getHeaders();
        
        if(headers.containsKey("Access-Control-Allow-Origin") 
                || headers.containsKey("Access-Control-Allow-Headers")
                || headers.containsKey("Access-Control-Allow-Credentials")
                || headers.containsKey("Access-Control-Allow-Methods")) {
            return;
        }
        
        headers.add("Access-Control-Allow-Origin", config.getCorsFilter().getOrigin());                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      
        headers.add("Access-Control-Allow-Headers", config.getCorsFilter().getHeaders());
        headers.add("Access-Control-Allow-Credentials", config.getCorsFilter().getCredentials());
        headers.add("Access-Control-Allow-Methods", config.getCorsFilter().getMethods());
    }
}
