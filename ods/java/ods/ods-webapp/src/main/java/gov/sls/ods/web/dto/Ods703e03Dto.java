package gov.sls.ods.web.dto;

import gov.sls.entity.ods.OdsResourceVersion;
import gov.sls.ods.dto.Ods703eTab2DialogDto;

import java.util.List;

import lombok.Data;

@Data
public class Ods703e03Dto {
    private Ods703eTab2DialogDto ods703eTab2DialogDto;
    private List<Ods703eTab2DialogDto> Ods703eTab2DialogDtoList;
    private List<OdsResourceVersion> odsResourceVersionList;
    private String packageId;
    private String packageVer;
    
    public Ods703e03Dto() {
        ods703eTab2DialogDto = new Ods703eTab2DialogDto();
    }

    public String getCatId() {
        return ods703eTab2DialogDto.getCatId();
    }

    public String getCatName() {
        return ods703eTab2DialogDto.getCatName();
    }

    public String getResDesc() {
        return ods703eTab2DialogDto.getResDesc();
    }

    public String getResName() {
        return ods703eTab2DialogDto.getResName();
    }

    public String getResourceId() {
        return ods703eTab2DialogDto.getResourceId();
    }

    public String getResourceVer() {
        return ods703eTab2DialogDto.getResourceVer();
    }

    public String getVer() {
        return ods703eTab2DialogDto.getVer();
    }

    public String getVersionDatetime() {
        return ods703eTab2DialogDto.getVersionDatetime();
    }

    public void setCatId(String catId) {
        ods703eTab2DialogDto.setCatId(catId);
    }

    public void setCatName(String catName) {
        ods703eTab2DialogDto.setCatName(catName);
    }

    public void setResDesc(String resDesc) {
        ods703eTab2DialogDto.setResDesc(resDesc);
    }

    public void setResName(String resName) {
        ods703eTab2DialogDto.setResName(resName);
    }

    public void setResourceId(String resourceId) {
        ods703eTab2DialogDto.setResourceId(resourceId);
    }

    public void setResourceVer(String resourceVer) {
        ods703eTab2DialogDto.setResourceVer(resourceVer);
    }

    public void setVer(String ver) {
        ods703eTab2DialogDto.setVer(ver);
    }

    public void setVersionDatetime(String versionDatetime) {
        ods703eTab2DialogDto.setVersionDatetime(versionDatetime);
    }

}
