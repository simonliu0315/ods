package gov.sls.ods.config;

import gov.sls.commons.core.Profiles;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OdsApplicationPropertiesAccessor {

    @Inject
    private Environment environment;

    private Profiles profile = null;

    public String getProperty(OdsApplicationProperties property) {
        String defaultValue = property.getDefaultValue(profile);
        return getProperty(property.getPropertyName(), defaultValue);
    }

    /**
     * 取得 boolean 型態的值。
     * 
     * @param property
     *            屬性，其值應該要是 boolean，或是 {@code "auto"}。
     * @param productionDefault
     *            如果屬性值為 {@code "auto"}，且是在 PRODUCTION 環境時的預設。
     * @param developmentDefault
     *            如果屬性值為 {@code "auto"}，且是在 DEVELOPMENT 環境時的預設。
     * @return 屬性的 boolean 值，如果屬性值為 {@code "auto"} 則視環境回傳指定的預設值。
     */
    public boolean getAutoBooleanProperty(OdsApplicationProperties property,
            boolean productionDefault, boolean developmentDefault) {
        String value = getProperty(property);

        if ("auto".equals(value)) {
            if (isInProductionProfile()) {
                return productionDefault;
            } else {
                return developmentDefault;
            }
        }

        return Boolean.parseBoolean(value);
    }

    public String getProperty(String name) {
        String value = null;

        OdsApplicationProperties property = OdsApplicationProperties.of(name);
        if (property != null) {
            value = getProperty(property);
        } else {
            value = environment.getProperty(name);
        }

        return value;
    }

    public String getProperty(String name, String defaultValue) {
        return environment.getProperty(name, defaultValue);
    }

    @PostConstruct
    public void check() {
        profile = getActiveEnvironmentProfile();

        log.debug("Checking application properties for profile: {}", profile);

        List<String> missedProperties = new ArrayList<String>();
        for (OdsApplicationProperties property : OdsApplicationProperties.values()) {
            String propertyName = property.getPropertyName();
            if (environment.containsProperty(propertyName)) {
                log.debug("Found property \"{}\" of value: {}", propertyName,
                        environment.getProperty(propertyName));

            } else if (property.getDefaultValueMap().containsKey(profile)) {
                log.debug("Missing property \"{}\", using default value: {}", propertyName,
                        property.getDefaultValueMap().get(profile));

            } else {
                // 如果沒有設定指定的設定值，而且我們也沒有提供預設值，就記起來之後再噴錯吧!
                log.warn("Property \"{}\" without defualt value has not been set.", propertyName);
                missedProperties.add(propertyName);
            }
        }

        if (!missedProperties.isEmpty()) {
            String message = "Following system properties are required in current profile \""
                    + profile.name() + "\": " + missedProperties;
            throw new IllegalStateException(message);
        }
    }

    public Profiles getActiveEnvironmentProfile() {
        if (Profiles.PRODUCTION.isProfileActivated(environment)) {
            return Profiles.PRODUCTION;
        } else {
            return Profiles.DEVELOPMENT;
        }
    }

    public boolean isInProductionProfile() {
        return getActiveEnvironmentProfile() == Profiles.PRODUCTION;
    }

    public boolean isInDevelopmentProfile() {
        return getActiveEnvironmentProfile() == Profiles.DEVELOPMENT;
    }
}
