/*
 * 
 */
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
 * The persistent class for the ODS_SOLR_CONTROLTABLE database table.
 * 
 */
@Entity
@Table(name="ODS_SOLR_CONTROLTABLE")
@NamedQuery(name="OdsSolrControltable.findAll", query="SELECT o FROM OdsSolrControltable o")
public class OdsSolrControltable implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GenericGenerator(name = "generator", strategy = "guid", parameters = {})
    @GeneratedValue(generator = "generator")
    @Column(name = "ID")
    private String id;

    @Column(name="EXECUTE_START_DATE")
    private Date executeStartDate;
    
    @Column(name="EXECUTE_END_DATE")
    private Date executeEndDate;

    @Column(name="EXECUTE_RESULT")
    private String executeResult;

    /**
     */
    public OdsSolrControltable() {
        super();
    }
    
    /**
     * @param id id
     */
    public OdsSolrControltable(String id) {
        super();
        this.id = id;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the executeStartDate
     */
    public Date getExecuteStartDate() {
        return executeStartDate;
    }

    /**
     * @param executeStartDate the executeStartDate to set
     */
    public void setExecuteStartDate(Date executeStartDate) {
        this.executeStartDate = executeStartDate;
    }

    /**
     * @return the executeEndDate
     */
    public Date getExecuteEndDate() {
        return executeEndDate;
    }

    /**
     * @param executeEndDate the executeEndDate to set
     */
    public void setExecuteEndDate(Date executeEndDate) {
        this.executeEndDate = executeEndDate;
    }

    /**
     * @return the executeResult
     */
    public String getExecuteResult() {
        return executeResult;
    }

    /**
     * @param executeResult the executeResult to set
     */
    public void setExecuteResult(String executeResult) {
        this.executeResult = executeResult;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof OdsSolrControltable)) {
            return false;
        }
        OdsSolrControltable other = (OdsSolrControltable) obj;
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        return true;
    }

}
