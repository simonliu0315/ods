package gov.sls.entity.ods;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name="ODS_ORG_CODE")
@NamedQuery(name="OdsOrgCode.findAll", query="SELECT o FROM OdsOrgCode o")
public class OdsOrgCode implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @EmbeddedId
    private OdsOrgCodePK id;
    
    @Column(name="HSN_NM")
    private String hsnNm;

    @Column(name="TOWN_NM")
    private String townNm;

    public OdsOrgCodePK getId() {
        return id;
    }

    public void setId(OdsOrgCodePK id) {
        this.id = id;
    }

    public String getHsnNm() {
        return hsnNm;
    }

    public void setHsnNm(String hsnNm) {
        this.hsnNm = hsnNm;
    }

    public String getTownNm() {
        return townNm;
    }

    public void setTownNm(String townNm) {
        this.townNm = townNm;
    }
    
    
}
