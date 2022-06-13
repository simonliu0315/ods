package gov.sls.entity.ads;


import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.annotation.Generated;
import javax.validation.constraints.NotNull;


/**
 * 資料表 ADS132FA 的 Class。
 *
 */
@Entity
@Table(name="ADS132FA")
@Generated(value = "com.cht.utils.modeler.export.EntityExporter", date = "2014-05-06T09:56:39+0800")
public class Ads132fa implements Serializable {
    // default serial version id, required for serializable classes.
    private static final long serialVersionUID = 1L;

    /**
     * 資料來源
     */
    private String scode;

    /**
     * 期別
     */
    @Id
    @NotNull
    private String ped;

    /**
     * 起始年度
     */
    private String styr;

    /**
     * 終止年度
     */
    private String edyr;

    /**
     * 修改者ID
     */
    private String userid;

    /**
     * 修改日期
     */
    private String mdate;

    /**
     * 修改時間
     */
    private String mtime;

    /**
     * Y-新年度轉機關，else-不轉
     */
    @Column(name="CHANGE_AGE")
    private String changeAge;

    /**
     * 期別名稱
     */
    private String nam;

    /**
     * 預設建構式。
     */
    public Ads132fa() {
    }

    /**
     * 取得 資料來源 的值。
     *
     * @return 資料來源 的值。
     */
    public String getScode() {
        return this.scode;
    }

    /**
     * 設定 資料來源 的值。
     *
     * @param scode
     *            資料來源 的值。
     */
    public void setScode (String scode) {
        this.scode = scode;
    }

    /**
     * 取得 期別 的值。
     *
     * @return 期別 的值。
     */
    public String getPed() {
        return this.ped;
    }

    /**
     * 設定 期別 的值。
     *
     * @param ped
     *            期別 的值。
     */
    public void setPed (String ped) {
        this.ped = ped;
    }

    /**
     * 取得 起始年度 的值。
     *
     * @return 起始年度 的值。
     */
    public String getStyr() {
        return this.styr;
    }

    /**
     * 設定 起始年度 的值。
     *
     * @param styr
     *            起始年度 的值。
     */
    public void setStyr (String styr) {
        this.styr = styr;
    }

    /**
     * 取得 終止年度 的值。
     *
     * @return 終止年度 的值。
     */
    public String getEdyr() {
        return this.edyr;
    }

    /**
     * 設定 終止年度 的值。
     *
     * @param edyr
     *            終止年度 的值。
     */
    public void setEdyr (String edyr) {
        this.edyr = edyr;
    }

    /**
     * 取得 修改者ID 的值。
     *
     * @return 修改者ID 的值。
     */
    public String getUserid() {
        return this.userid;
    }

    /**
     * 設定 修改者ID 的值。
     *
     * @param userid
     *            修改者ID 的值。
     */
    public void setUserid (String userid) {
        this.userid = userid;
    }

    /**
     * 取得 修改日期 的值。
     *
     * @return 修改日期 的值。
     */
    public String getMdate() {
        return this.mdate;
    }

    /**
     * 設定 修改日期 的值。
     *
     * @param mdate
     *            修改日期 的值。
     */
    public void setMdate (String mdate) {
        this.mdate = mdate;
    }

    /**
     * 取得 修改時間 的值。
     *
     * @return 修改時間 的值。
     */
    public String getMtime() {
        return this.mtime;
    }

    /**
     * 設定 修改時間 的值。
     *
     * @param mtime
     *            修改時間 的值。
     */
    public void setMtime (String mtime) {
        this.mtime = mtime;
    }

    /**
     * 取得 Y-新年度轉機關，else-不轉 的值。
     *
     * @return Y-新年度轉機關，else-不轉 的值。
     */
    public String getChangeAge() {
        return this.changeAge;
    }

    /**
     * 設定 Y-新年度轉機關，else-不轉 的值。
     *
     * @param changeAge
     *            Y-新年度轉機關，else-不轉 的值。
     */
    public void setChangeAge (String changeAge) {
        this.changeAge = changeAge;
    }

    /**
     * 取得 期別名稱 的值。
     *
     * @return 期別名稱 的值。
     */
    public String getNam() {
        return this.nam;
    }

    /**
     * 設定 期別名稱 的值。
     *
     * @param nam
     *            期別名稱 的值。
     */
    public void setNam (String nam) {
        this.nam = nam;
    }


    /** {@inheritDoc} */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Ads132fa [");
        builder.append("scode=");
        builder.append(getScode());
        builder.append(", ped=");
        builder.append(getPed());
        builder.append(", styr=");
        builder.append(getStyr());
        builder.append(", edyr=");
        builder.append(getEdyr());
        builder.append(", userid=");
        builder.append(getUserid());
        builder.append(", mdate=");
        builder.append(getMdate());
        builder.append(", mtime=");
        builder.append(getMtime());
        builder.append(", changeAge=");
        builder.append(getChangeAge());
        builder.append(", nam=");
        builder.append(getNam());
        builder.append("]");
        return builder.toString();
    }

}
