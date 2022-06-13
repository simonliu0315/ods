package gov.sls.ods.web.config;

import gov.sls.commons.core.security.login.impl.SlsSacUaaEnvironments;

public class OdsSlsSacUaaEnvironments extends SlsSacUaaEnvironments {

    public String getFronKey() {
        return super.SLS3Front;
    }

    public String getBackKey() {
        return super.SLS3Back;
    }

}
