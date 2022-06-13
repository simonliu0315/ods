package gov.sls.ods.repository;

import java.util.Date;

public interface OdsSolrControltableRepositoryCustom {

    /**取得上一次處理成功時間
     * @return Date
     */
    public Date getPreExecuteDate();
}
