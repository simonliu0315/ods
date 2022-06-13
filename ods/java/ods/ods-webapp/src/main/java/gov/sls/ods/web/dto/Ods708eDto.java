package gov.sls.ods.web.dto;

import gov.sls.ods.dto.Ods708eGridDto;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class Ods708eDto {
    private Ods708eGridDto ods708eGridDto;
    private List<Ods708eGridDto> ods708eGridDtoList;
    
    public Ods708eDto() {
        ods708eGridDto = new Ods708eGridDto();
    }

    /**
     * @return
     * @see gov.sls.ods.dto.Ods708eGridDto#getMethodName()
     */
    public String getMethodName() {
        return ods708eGridDto.getMethodName();
    }

    /**
     * @return
     * @see gov.sls.ods.dto.Ods708eGridDto#getServiceName()
     */
    public String getServiceName() {
        return ods708eGridDto.getServiceName();
    }

    /**
     * @param methodName
     * @see gov.sls.ods.dto.Ods708eGridDto#setMethodName(java.lang.String)
     */
    public void setMethodName(String methodName) {
        ods708eGridDto.setMethodName(methodName);
    }

    /**
     * @param serviceName
     * @see gov.sls.ods.dto.Ods708eGridDto#setServiceName(java.lang.String)
     */
    public void setServiceName(String serviceName) {
        ods708eGridDto.setServiceName(serviceName);
    }

    /**
     * @return
     * @see gov.sls.ods.dto.Ods708eGridDto#getEndDate()
     */
    public Date getEndDate() {
        return ods708eGridDto.getEndDate();
    }

    /**
     * @return
     * @see gov.sls.ods.dto.Ods708eGridDto#getStartDate()
     */
    public Date getStartDate() {
        return ods708eGridDto.getStartDate();
    }

    /**
     * @param endDate
     * @see gov.sls.ods.dto.Ods708eGridDto#setEndDate(java.lang.String)
     */
    public void setEndDate(Date endDate) {
        ods708eGridDto.setEndDate(endDate);
    }

    /**
     * @param startDate
     * @see gov.sls.ods.dto.Ods708eGridDto#setStartDate(java.lang.String)
     */
    public void setStartDate(Date startDate) {
        ods708eGridDto.setStartDate(startDate);
    }

    /**
     * @return
     * @see gov.sls.ods.dto.Ods708eGridDto#getDesc()
     */
    public String getDesc() {
        return ods708eGridDto.getDesc();
    }

    /**
     * @param desc
     * @see gov.sls.ods.dto.Ods708eGridDto#setDesc(java.lang.String)
     */
    public void setDesc(String desc) {
        ods708eGridDto.setDesc(desc);
    }

}
