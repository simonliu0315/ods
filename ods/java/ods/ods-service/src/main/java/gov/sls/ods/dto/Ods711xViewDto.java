package gov.sls.ods.dto;

import java.util.Date;

import gov.sls.entity.ods.OdsDanResourceStus;
import lombok.Data;

/**
 * 標註 {@code @Data}，lombok 會自動幫你產生 getter/setter 還 {@link #hashCode()},
 * {@link #equals(Object)} 及 {@link #toString()} 等 method。
 * 
 * @see http://projectlombok.org/　
 */
@Data
public class Ods711xViewDto {
    private OdsDanResourceStus odsDanResourceStus;
    private Integer maxVer;
    private String name;
    private String odsResourceId;
    private String format;
    private String workbookId;
    
    public Ods711xViewDto(){
        odsDanResourceStus = new OdsDanResourceStus();
    }

    public String getId() {
        return odsDanResourceStus.getId();
    }

    public void setId(String id) {
        odsDanResourceStus.setId(id);
    }

    public String getResourceId() {
        return odsDanResourceStus.getResourceId();
    }

    public void setResourceId(String resourceId) {
        odsDanResourceStus.setResourceId(resourceId);
    }

    public String getResourceType() {
        return odsDanResourceStus.getResourceType();
    }

    public void setResourceType(String resourceType) {
        odsDanResourceStus.setResourceType(resourceType);
    }

    public Date getRefreshTime() {
        return odsDanResourceStus.getRefreshTime();
    }

    public void setRefreshTime(Date refreshTime) {
        odsDanResourceStus.setRefreshTime(refreshTime);
    }

    public String getExportStus() {
        return odsDanResourceStus.getExportStus();
    }

    public void setExportStus(String exportStus) {
        odsDanResourceStus.setExportStus(exportStus);
    }

    public String getImportOdsStus() {
        return odsDanResourceStus.getImportOdsStus();
    }

    public void setImportOdsStus(String importOdsStus) {
        odsDanResourceStus.setImportOdsStus(importOdsStus);
    }
    
    
}
