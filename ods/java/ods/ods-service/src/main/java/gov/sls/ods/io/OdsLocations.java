package gov.sls.ods.io;

import gov.sls.commons.core.io.Location;

public class OdsLocations {
    
    public enum Persistent implements Location {

        IMG_IN_PATH_ROOT("/");

        private final String pattern;

        private Persistent(String pattern) {
            this.pattern = pattern;
        }

        @Override
        public String getPattern() {
            return pattern;
        }
    }
    
}
