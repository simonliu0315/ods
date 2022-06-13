package gov.sls.entity.ods;

import java.io.Serializable;
import java.lang.Integer;
import java.lang.String;
import java.util.Date;

import javax.persistence.*;

/**
 * Entity implementation class for Entity: OdsDanWorkbook
 *
 */
@Entity
@Table(name="ODS_DAN_WORKBOOK")

public class OdsDanWorkbook implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    private String id;
    private String name;
    private Integer size;
    private String path;
    @Column(name="REPOSITORY_URL")
    private String repositoryUrl;
    
    @Column(name="UPDATED_AT")
    private Date updatedAt;
    
    @Column(name="CREATE_AT")
    private Date createdAt;
    

    public OdsDanWorkbook() {
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
    public Integer getSize() {
        return this.size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }   
    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }   
    
    public String getRepositoryUrl() {
        return repositoryUrl;
    }

    public void setRepositoryUrl(String repositoryUrl) {
        this.repositoryUrl = repositoryUrl;
    }

    public Date getUpdatedAt() {
        return this.updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }   
    public Date getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "OdsDanWorkbook [id=" + id + ", name=" + name + ", size=" + size
                + ", path=" + path + ", repositoryUrl=" + repositoryUrl
                + ", updatedAt=" + updatedAt + ", createdAt=" + createdAt + "]";
    }
   
    
}
