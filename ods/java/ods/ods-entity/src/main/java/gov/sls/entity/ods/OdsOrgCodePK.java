package gov.sls.entity.ods;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class OdsOrgCodePK implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Column(name="HSN_CD")
    private String hsnCd;

    @Column(name="TOWN_CD")
    private String townCd;

    public String getHsnCd() {
        return hsnCd;
    }

    public void setHsnCd(String hsnCd) {
        this.hsnCd = hsnCd;
    }

    public String getTownCd() {
        return townCd;
    }

    public void setTownCd(String townCd) {
        this.townCd = townCd;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((hsnCd == null) ? 0 : hsnCd.hashCode());
        result = prime * result + townCd.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        OdsOrgCodePK other = (OdsOrgCodePK) obj;
        if (hsnCd == null) {
            if (other.hsnCd != null)
                return false;
        } else if (!hsnCd.equals(other.hsnCd))
            return false;
        if (townCd != other.townCd)
            return false;
        return true;
    }

}
