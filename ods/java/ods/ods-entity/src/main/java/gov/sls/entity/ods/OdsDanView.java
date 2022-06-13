package gov.sls.entity.ods;

import java.io.Serializable;
import java.lang.Integer;
import java.lang.String;
import java.util.Date;

import javax.persistence.*;

/**
 * Entity implementation class for Entity: OdsDanView
 *
 */
@Entity
@Table(name="ODS_DAN_VIEW")

public class OdsDanView implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    private String id;
    @Column(name="WORKBOOK_ID")
    private String workbookId;
    private String name;
    private String title;
    @Column(name="[INDEX]")
    private Integer index;
    @Column(name="REPOSITORY_URL")
    private String repositoryUrl;
    @Column(name="PREVIEW_URL")
    private String previewUrl;
    
    
    @Column(name="UPDATED_AT")
    private Date updatedAt;
    
    @Column(name="CREATE_AT")
    private Date createdAt;

    public OdsDanView() {
        super();
    }   
    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }   
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }   
    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }   
    public Integer getIndex() {
        return this.index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }   
    public String getRepositoryUrl() {
        return this.repositoryUrl;
    }

    public void setRepositoryUrl(String repositoryUrl) {
        this.repositoryUrl = repositoryUrl;
    }   
    public String getPreviewUrl() {
        return this.previewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }
    public Date getUpdatedAt() {
        return updatedAt;
    }
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
    public Date getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    public String getWorkbookId() {
        return workbookId;
    }
    public void setWorkbookId(String workbookId) {
        this.workbookId = workbookId;
    }
    @Override
    public String toString() {
        return "OdsDanView [id=" + id + ", workbookId=" + workbookId
                + ", name=" + name + ", title=" + title + ", index=" + index
                + ", repositoryUrl=" + repositoryUrl + ", previewUrl="
                + previewUrl + ", updatedAt=" + updatedAt + ", createdAt="
                + createdAt + "]";
    }
    
   
}
