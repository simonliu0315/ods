package gov.sls.ods.io;

public enum OdsProcessConstant {
    FIRST("|#"),LAST("#|"),DELIMITER("*|");
    
    private String token;
    OdsProcessConstant(String token){
        this.token = token;
    }

    public String getToken() {
        return this.token;
    }	
}
