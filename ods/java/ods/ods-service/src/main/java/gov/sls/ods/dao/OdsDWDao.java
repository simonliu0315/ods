package gov.sls.ods.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface OdsDWDao {

    /**
     * 查詢
     * 
     * @param tablename
     *            動態sql查詢對象tablename
     * @param start
     *            start
     * @param end
     *            end
     * @param dateFeildName
     *            動態sql查詢時間欄位
     * @param dbname
     *            動態sql查詢對象db
     * @return
     */
    public List<Map<String, Object>> findDWHStageData(String tablename,
            Date start, Date end, String dateFeildName, String dbname);

    /**
     * 分頁查詢
     * 
     * @param tablename
     *            動態sql查詢對象tablename
     * @param start
     *            start
     * @param end
     *            end
     * @param dateFeildName
     *            動態sql查詢時間欄位
     * @param dbname
     *            動態sql查詢對象db
     * @param offset
     *            查詢開始rownum
     * @param limit
     *            查詢筆數
     * @param orderId
     *            排序ID欄位名稱
     * @return
     */
    public List<Map<String, Object>> findDWHStageDataPage(String tablename,
            Date start, Date end, String dateFeildName, String dbname,
            int offset, int limit, String orderId);

    /**
     * 查詢總筆數
     * 
     * @param tablename
     *            動態sql查詢對象tablename
     * @param start
     *            start
     * @param end
     *            end
     * @param dateFeildName
     *            動態sql查詢時間欄位
     * @param dbname
     *            動態sql查詢對象db
     * @return 總筆數
     */
    public int countDWHStageData(String tablename, Date start, Date end,
            String dateFeildName, String dbname);
}
