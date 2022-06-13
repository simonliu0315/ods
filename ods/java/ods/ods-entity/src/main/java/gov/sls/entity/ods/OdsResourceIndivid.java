package gov.sls.entity.ods;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;


/**
 * The persistent class for the ODS_RESOURCE_INDIVID database table.
 * 
 */
@Entity
@Table(name="ODS_RESOURCE_INDIVID")
@NamedQuery(name="OdsResourceIndivid.findAll", query="SELECT o FROM OdsResourceIndivid o")
public class OdsResourceIndivid implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GenericGenerator(name="generator", strategy="guid", parameters= {})
    @GeneratedValue(generator="generator")
    @Column(name="ID")
    private String id;

    @Column(name="DAN_IMPORT_DATE")
    private Date danImportDate;

    @Column(name="DAN_IMPORT_LOG")
    private String danImportLog;

    @Column(name="DAN_IMPORT_MK")
    private String danImportMk;

    @Column(name="REPORT_NOTIFY_DATE")
    private Date reportNotifyDate;

    @Column(name="REPORT_NOTIFY_LOG")
    private String reportNotifyLog;

    @Column(name="REPORT_NOTIFY_MK")
    private String reportNotifyMk;

    @Column(name="RESOURCE_DATE")
    private String resourceDate;

    @Column(name="RESOURCE_ID")
    private String resourceId;

    @Column(name="USER_UNIFY_ID")
    private String userUnifyId;

    public OdsResourceIndivid() {
    }
    
    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getDanImportDate() {
        return this.danImportDate;
    }

    public void setDanImportDate(Date danImportDate) {
        this.danImportDate = danImportDate;
    }

    public String getDanImportLog() {
        return this.danImportLog;
    }

    public void setDanImportLog(String danImportLog) {
        this.danImportLog = danImportLog;
    }

    public String getDanImportMk() {
        return this.danImportMk;
    }

    public void setDanImportMk(String danImportMk) {
        this.danImportMk = danImportMk;
    }

    public Date getReportNotifyDate() {
        return this.reportNotifyDate;
    }

    public void setReportNotifyDate(Date reportNotifyDate) {
        this.reportNotifyDate = reportNotifyDate;
    }

    public String getReportNotifyLog() {
        return this.reportNotifyLog;
    }

    public void setReportNotifyLog(String reportNotifyLog) {
        this.reportNotifyLog = reportNotifyLog;
    }

    public String getReportNotifyMk() {
        return this.reportNotifyMk;
    }

    public void setReportNotifyMk(String reportNotifyMk) {
        this.reportNotifyMk = reportNotifyMk;
    }

    public String getResourceDate() {
        return this.resourceDate;
    }

    public void setResourceDate(String resourceDate) {
        this.resourceDate = resourceDate;
    }

    public String getResourceId() {
        return this.resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getUserUnifyId() {
        return this.userUnifyId;
    }

    public void setUserUnifyId(String userUnifyId) {
        this.userUnifyId = userUnifyId;
    }

}
