package gov.sls.ods.web.dto;

import gov.sls.ods.dto.Ods706eTab1FormBean;
import gov.sls.ods.dto.Ods706eTab2FormBean;

import java.util.List;

import lombok.Data;

@Data
public class Ods706eDto {
    private Ods706eTab1FormBean ods706eTab1FormBean;
    private List<Ods706eTab2FormBean> ods706eTab2FormBeanList;
    private String id;
    private String rowCount;
    
    public Ods706eDto() {
        ods706eTab1FormBean = new Ods706eTab1FormBean();
    }

    public String getCriId() {
        return ods706eTab1FormBean.getCriId();
    }

    public String getDescription() {
        return ods706eTab1FormBean.getDescription();
    }

    public String getName() {
        return ods706eTab1FormBean.getName();
    }

    public String getResId() {
        return ods706eTab1FormBean.getResId();
    }

    public void setCriId(String criId) {
        ods706eTab1FormBean.setCriId(criId);
    }

    public void setDescription(String description) {
        ods706eTab1FormBean.setDescription(description);
    }

    public void setName(String name) {
        ods706eTab1FormBean.setName(name);
    }

    public void setResId(String resId) {
        ods706eTab1FormBean.setResId(resId);
    }
    
    
}
