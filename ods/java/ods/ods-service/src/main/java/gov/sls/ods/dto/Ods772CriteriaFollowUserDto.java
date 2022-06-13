/**
 * 
 */
package gov.sls.ods.dto;

import java.util.Date;

import javax.persistence.Column;

import lombok.Data;

/**
 * 標註 {@code @Data}，lombok 會自動幫你產生 getter/setter 還 {@link #hashCode()}, {@link #equals(Object)} 及
 * {@link #toString()} 等 method。
 * 
 * @see http://projectlombok.org/　
 */
public class Ods772CriteriaFollowUserDto {
    private String criteriaId;
    private String packageId;
    private String resourceId;
    private String userId;
    private int ver;
    private String aggFunc;
    /**
     * @return the criteriaId
     */
    public String getCriteriaId() {
        return criteriaId;
    }
    /**
     * @param criteriaId the criteriaId to set
     */
    public void setCriteriaId(String criteriaId) {
        this.criteriaId = criteriaId;
    }
    /**
     * @return the packageId
     */
    public String getPackageId() {
        return packageId;
    }
    /**
     * @param packageId the packageId to set
     */
    public void setPackageId(String packageId) {
        this.packageId = packageId;
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
    /**
     * @return the ver
     */
    public int getVer() {
        return ver;
    }
    /**
     * @param ver the ver to set
     */
    public void setVer(int ver) {
        this.ver = ver;
    }
    /**
     * @return the aggFunc
     */
    public String getAggFunc() {
        return aggFunc;
    }
    /**
     * @param aggFunc the aggFunc to set
     */
    public void setAggFunc(String aggFunc) {
        this.aggFunc = aggFunc;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((criteriaId == null) ? 0 : criteriaId.hashCode());
        result = prime * result + ((packageId == null) ? 0 : packageId.hashCode());
        result = prime * result + ((resourceId == null) ? 0 : resourceId.hashCode());
        result = prime * result + ((userId == null) ? 0 : userId.hashCode());
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
        if (!(obj instanceof Ods772CriteriaFollowUserDto)) {
            return false;
        }
        Ods772CriteriaFollowUserDto other = (Ods772CriteriaFollowUserDto) obj;
        if (criteriaId == null) {
            if (other.criteriaId != null) {
                return false;
            }
        } else if (!criteriaId.equals(other.criteriaId)) {
            return false;
        }
        if (packageId == null) {
            if (other.packageId != null) {
                return false;
            }
        } else if (!packageId.equals(other.packageId)) {
            return false;
        }
        if (resourceId == null) {
            if (other.resourceId != null) {
                return false;
            }
        } else if (!resourceId.equals(other.resourceId)) {
            return false;
        }
        if (userId == null) {
            if (other.userId != null) {
                return false;
            }
        } else if (!userId.equals(other.userId)) {
            return false;
        }
        return true;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Ods772CriteriaFollowUserDto [criteriaId=");
        builder.append(criteriaId);
        builder.append(", packageId=");
        builder.append(packageId);
        builder.append(", resourceId=");
        builder.append(resourceId);
        builder.append(", userId=");
        builder.append(userId);
        builder.append(", ver=");
        builder.append(ver);
        builder.append(", aggFunc=");
        builder.append(aggFunc);
        builder.append("]");
        return builder.toString();
    }
    
    
}
