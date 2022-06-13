/**
 * 
 */
package gov.sls.ods.dto;

import gov.sls.entity.ods.OdsCriteriaPK;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 標註 {@code @Data}，lombok 會自動幫你產生 getter/setter 還 {@link #hashCode()}, {@link #equals(Object)} 及
 * {@link #toString()} 等 method。
 * 
 * @see http://projectlombok.org/　
 */
public class Ods772CriteriaResourceDto {

    private String resourceId;

    private String resourceCriteriaId;

    private int condition;
    
    private String name;
    
    private String aggregateFunc;

    private String createUserId;

    private Date created;

    private String dataField;

    private String operator;

    private String target;

    private String updateUserId;

    private Date updated;
    
    private Integer maxVer;

    public Ods772CriteriaResourceDto() {
    }

    @JsonIgnore
    public OdsCriteriaPK getId() {
        OdsCriteriaPK id = new OdsCriteriaPK();
        id.setResourceId(this.resourceId);
        id.setCondition(this.condition);
        id.setResourceCriteriaId(this.resourceCriteriaId);
        return id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    public String getAggregateFunc() {
        return this.aggregateFunc;
    }

    public void setAggregateFunc(String aggregateFunc) {
        this.aggregateFunc = aggregateFunc;
    }

    public String getCreateUserId() {
        return this.createUserId;
    }

    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId;
    }

    public Date getCreated() {
        return this.created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getDataField() {
        return this.dataField;
    }

    public void setDataField(String dataField) {
        this.dataField = dataField;
    }

    public String getOperator() {
        return this.operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getTarget() {
        return this.target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getUpdateUserId() {
        return this.updateUserId;
    }

    public void setUpdateUserId(String updateUserId) {
        this.updateUserId = updateUserId;
    }

    public Date getUpdated() {
        return this.updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public Integer getMaxVer() {
        return maxVer;
    }

    public void setMaxVer(Integer maxVer) {
        this.maxVer = maxVer;
    }

    /**
     * @return the resourceId
     */
    public String getResourceId() {
        return resourceId;
    }

    /**
     * @param resourceId the resourceId to set
     */
    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    /**
     * @return the criteriaId
     */
    public String getResourceCriteriaId() {
        return resourceCriteriaId;
    }

    /**
     * @param criteriaId the criteriaId to set
     */
    public void setResourceCriteriaId(String resourceCriteriaId) {
        this.resourceCriteriaId = resourceCriteriaId;
    }

    /**
     * @return the condition
     */
    public int getCondition() {
        return condition;
    }

    /**
     * @param condition the condition to set
     */
    public void setCondition(int condition) {
        this.condition = condition;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + condition;
        result = prime * result + ((resourceCriteriaId == null) ? 0 : resourceCriteriaId.hashCode());
        result = prime * result + ((resourceId == null) ? 0 : resourceId.hashCode());
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
        if (!(obj instanceof Ods772CriteriaResourceDto)) {
            return false;
        }
        Ods772CriteriaResourceDto other = (Ods772CriteriaResourceDto) obj;
        if (condition != other.condition) {
            return false;
        }
        if (resourceCriteriaId == null) {
            if (other.resourceCriteriaId != null) {
                return false;
            }
        } else if (!resourceCriteriaId.equals(other.resourceCriteriaId)) {
            return false;
        }
        if (resourceId == null) {
            if (other.resourceId != null) {
                return false;
            }
        } else if (!resourceId.equals(other.resourceId)) {
            return false;
        }
        return true;
    }

}
