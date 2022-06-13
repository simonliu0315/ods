package gov.sls.ods.web.dto;

import gov.sls.ods.dto.Ods707eGridDto;

import java.util.List;

import lombok.Data;

@Data
public class Ods707eDto {
    private Ods707eGridDto ods707eGridDto;
    private List<Ods707eGridDto> ods707eGridDtoList;
    
    public Ods707eDto() {
        ods707eGridDto = new Ods707eGridDto();
    }

    public String getAvgRate() {
        return ods707eGridDto.getAvgRate();
    }

    public String getClickCount() {
        return ods707eGridDto.getClickCount();
    }

    public String getEndDate() {
        return ods707eGridDto.getEndDate();
    }

    public String getFollowCount() {
        return ods707eGridDto.getFollowCount();
    }

    public String getPackageDescription() {
        return ods707eGridDto.getPackageDescription();
    }

    public String getPackageId() {
        return ods707eGridDto.getPackageId();
    }

    public String getPackageName() {
        return ods707eGridDto.getPackageName();
    }

    public String getStartDate() {
        return ods707eGridDto.getStartDate();
    }

    public String getTotCount() {
        return ods707eGridDto.getTotCount();
    }

    public void setAvgRate(String avgRate) {
        ods707eGridDto.setAvgRate(avgRate);
    }

    public void setClickCount(String clickCount) {
        ods707eGridDto.setClickCount(clickCount);
    }

    public void setEndDate(String endDate) {
        ods707eGridDto.setEndDate(endDate);
    }

    public void setFollowCount(String followCount) {
        ods707eGridDto.setFollowCount(followCount);
    }

    public void setPackageDescription(String packageDescription) {
        ods707eGridDto.setPackageDescription(packageDescription);
    }

    public void setPackageId(String packageId) {
        ods707eGridDto.setPackageId(packageId);
    }

    public void setPackageName(String packageName) {
        ods707eGridDto.setPackageName(packageName);
    }

    public void setStartDate(String startDate) {
        ods707eGridDto.setStartDate(startDate);
    }

    public void setTotCount(String totCount) {
        ods707eGridDto.setTotCount(totCount);
    }
    
}
