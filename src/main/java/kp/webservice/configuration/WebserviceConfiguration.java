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
