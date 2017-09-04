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
package kp.webservice.configuration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class WebserviceConfiguration {

    @Valid
    @NotNull
    public String adminResourceRole = "";
    
    @Valid
    @NotNull
    private boolean registerCORSFilter = true;

    @Valid
    @NotNull
    private boolean registerServerProperties = true;

    @Valid
    @NotNull
    private boolean registerRolesAllowedDynamicFeature = true;

    @Valid
    @NotNull
    private String rateUnit = "seconds";
    
    @Valid
    @NotNull
    private String durationUnit = "seconds";

    public String getAdminResourceRole() {
        return adminResourceRole;
    }

    public void setAdminResourceRole(String adminResourceRole) {
        this.adminResourceRole = adminResourceRole;
    }

    public boolean isRegisterCORSFilter() {
        return registerCORSFilter;
    }

    public void setRegisterCORSFilter(boolean registerCORSFilter) {
        this.registerCORSFilter = registerCORSFilter;
    }

    public boolean isRegisterServerProperties() {
        return registerServerProperties;
    }

    public void setRegisterServerProperties(boolean registerServerProperties) {
        this.registerServerProperties = registerServerProperties;
    }

    public boolean isRegisterRolesAllowedDynamicFeature() {
        return registerRolesAllowedDynamicFeature;
    }

    public void setRegisterRolesAllowedDynamicFeature(boolean registerRolesAllowedDynamicFeature) {
        this.registerRolesAllowedDynamicFeature = registerRolesAllowedDynamicFeature;
    }

    public String getRateUnit() {
        return rateUnit;
    }

    public void setRateUnit(String rateUnit) {
        this.rateUnit = rateUnit;
    }

    public String getDurationUnit() {
        return durationUnit;
    }

    public void setDurationUnit(String durationUnit) {
        this.durationUnit = durationUnit;
    }

}
