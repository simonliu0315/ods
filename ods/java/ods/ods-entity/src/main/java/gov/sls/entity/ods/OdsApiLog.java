package gov.sls.entity.ods;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * Entity implementation class for Entity: OdsApiLog
 *
 */
@Entity
@Table(name="ODS_API_LOG")
@NamedQuery(name="OdsApiLog.findAll", query="SELECT o FROM OdsApiLog o")
public class OdsApiLog implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name="METHOD_ID")
    private String methodId;
    
    @Id
    @Column(name="IP")
    private String ip;
    
    @Id
    @Column(name="REQUEST_DATE")
    private String requestDate;
    
    @Column(name="PACKAGE_ID")
    private String packageId;
    
    @Column(name="PACKAGE_VER")
    private Integer packageVer;
    
    @Column(name="CREATED")
    private Date created;
    
    @Column(name="REQUEST")
    private String request;

    @Column(name="STATUS")
    private String status;

    @Column(name="APP_ID")
    private String appId;

    @Column(name="USER_ID")
    private String userId;
    
    public String getMethodId() {
        return methodId;
    }

    public void setMethodId(String methodId) {
        this.methodId = methodId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(String requestDate) {
        this.requestDate = requestDate;
    }

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public Integer getPackageVer() {
        return packageVer;
    }

    public void setPackageVer(Integer packageVer) {
        this.packageVer = packageVer;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the appId
     */
    public String getAppId() {
        return appId;
    }

    /**
     * @param appId the appId to set
     */
    public void setAppId(String appId) {
        this.appId = appId;
    }

    /**
     * @return the userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * @param userId the userId to set
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    

}
