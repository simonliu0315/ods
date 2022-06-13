package gov.sls.ods.config;

import gov.sls.commons.core.Profiles;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

public enum OdsApplicationProperties {

    ENVIRONMENT_SHARED_PATH("ods.environment.sharedPath", ImmutableMap.of(Profiles.PRODUCTION,
            "/ods/", Profiles.DEVELOPMENT, "/ods/")),
    
    ENVIRONMENT_PUBLIC_PATH("ods.environment.sharedPath", ImmutableMap.of(Profiles.PRODUCTION,
            "/ods/public/", Profiles.DEVELOPMENT, "/ods/public/")),
    
    ENVIRONMENT_ADMIN_PATH("ods.environment.sharedPath", ImmutableMap.of(Profiles.PRODUCTION,
            "/ods/admin/", Profiles.DEVELOPMENT, "/ods/admin/")),
            
    ENVIRONMENT_DAN_RESOURCE_PATH("ods.environment.sharedPath", ImmutableMap.of(Profiles.PRODUCTION,
            "/comdat/ods/dan_resource/", Profiles.DEVELOPMENT, "/comdat/ods/dan_resource/"));
    
    private final String propertyName;
    private final Map<Profiles, String> defaultValueMap;

    private OdsApplicationProperties(String propertyName, Map<Profiles, String> defaultValueMap) {
        this.propertyName = propertyName;
        this.defaultValueMap = defaultValueMap;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public Map<Profiles, String> getDefaultValueMap() {
        return defaultValueMap;
    }

    public String getDefaultValue(Profiles profile) {
        return defaultValueMap.get(profile);
    }

    /**
     * 由 Properties 鍵值找到 {@code ApplicationProperties} 物件。
     * 
     * @param name
     *            鍵值。
     * @return 對應的 {@code ApplicationProperties} 物件，如果找不到就回傳 {@code null}。
     */
    public static OdsApplicationProperties of(String name) {
        for (OdsApplicationProperties property : values()) {
            if (property.getPropertyName().equals(name)) {
                return property;
            }
        }

        return null;
    }

}
