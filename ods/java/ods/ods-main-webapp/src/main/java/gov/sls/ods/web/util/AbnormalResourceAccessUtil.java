package gov.sls.ods.web.util;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AbnormalResourceAccessUtil {

    static final String ABNORMAL_RESOURCE_ACCESS_KEY = "Abnormal_Access";
    static final int ABNORMAL_RESOURCE_ACCESS_LIMIT = 5;
    static final String ABNORMAL_RESOURCE_ACCESS_OBJECT_LOGINAPPID = "loginAppId";
    static final String ABNORMAL_RESOURCE_ACCESS_OBJECT_LOGINFAILCOUNT = "loginFailCount";
    static final String ABNORMAL_RESOURCE_ACCESS_OBJECT_LOGINDATE = "loginDate";
    static final long HOUR24 = 24 * 60 * 60 * 1000;
    
    /**
     * 判斷是否為異常存取 (24 小時內有5次登入失敗)
     * @param request
     * @param appId
     * @return
     */
    public static boolean isAbnormalAccess(HttpServletRequest request,String appId) {
        if ( null == request.getServletContext().getAttribute(ABNORMAL_RESOURCE_ACCESS_KEY) ) {
            request.getServletContext().setAttribute(ABNORMAL_RESOURCE_ACCESS_KEY, new HashMap<String,Object>());
        }
        Map<String,Map<String,Object>> aaMap=(Map<String,Map<String,Object>>)request.getServletContext().getAttribute(ABNORMAL_RESOURCE_ACCESS_KEY);
        if ( aaMap.containsKey(appId) ) { // 在 blocklist
            Map<String,Object> aa=aaMap.get(appId);
            int loginFailCount=((Integer)aa.get(ABNORMAL_RESOURCE_ACCESS_OBJECT_LOGINFAILCOUNT)).intValue();
            Date loginDate=(Date)aa.get(ABNORMAL_RESOURCE_ACCESS_OBJECT_LOGINDATE);
            if ( (Calendar.getInstance().getTimeInMillis() - loginDate.getTime()) <= HOUR24 &&
                    loginFailCount > ABNORMAL_RESOURCE_ACCESS_LIMIT ) { // 重置登入失敗記錄
                return true;
            }
        }
        return false;
    }
    
    /**
     * 更新 application scope 異常存取狀態
     * @param request
     * @param appId
     */
    public static void updateAbnormalAccess(HttpServletRequest request,String appId) {
        Map<String,Map<String,Object>> aaMap=(Map<String,Map<String,Object>>)request.getServletContext().getAttribute(ABNORMAL_RESOURCE_ACCESS_KEY);
        if ( aaMap.containsKey(appId) ) { // 在 blocklist
            Map<String,Object> aa=aaMap.get(appId);
            int loginFailCount=((Integer)aa.get(ABNORMAL_RESOURCE_ACCESS_OBJECT_LOGINFAILCOUNT)).intValue();
            Date loginDate=(Date)aa.get(ABNORMAL_RESOURCE_ACCESS_OBJECT_LOGINDATE);
            if ( (Calendar.getInstance().getTimeInMillis() - loginDate.getTime()) > HOUR24 ) { // 重置登入失敗記錄
                aa.put(ABNORMAL_RESOURCE_ACCESS_OBJECT_LOGINFAILCOUNT, new Integer(1));
                aa.put(ABNORMAL_RESOURCE_ACCESS_OBJECT_LOGINDATE, new Date());
                aaMap.put(appId, aa);
                log.info(appId+" abnormal count reset to 1,"+aa.get(ABNORMAL_RESOURCE_ACCESS_OBJECT_LOGINDATE));
            } else { // 登入失敗記錄 + 1
                aa.put(ABNORMAL_RESOURCE_ACCESS_OBJECT_LOGINFAILCOUNT, new Integer(loginFailCount+1));
                aa.put(ABNORMAL_RESOURCE_ACCESS_OBJECT_LOGINDATE, new Date());
                aaMap.put(appId, aa);
                log.info(appId+" abnormal count set to "+aa.get(ABNORMAL_RESOURCE_ACCESS_OBJECT_LOGINFAILCOUNT)+","+aa.get(ABNORMAL_RESOURCE_ACCESS_OBJECT_LOGINDATE));
            }
        } else { // 不在 blocklist
            Map<String,Object> aa=new HashMap<String,Object>();
            aa.put(ABNORMAL_RESOURCE_ACCESS_OBJECT_LOGINAPPID, appId);
            aa.put(ABNORMAL_RESOURCE_ACCESS_OBJECT_LOGINFAILCOUNT, new Integer(1));
            aa.put(ABNORMAL_RESOURCE_ACCESS_OBJECT_LOGINDATE, new Date());
            aaMap.put(appId, aa);
            log.info(appId+" abnormal count set to 1,"+aa.get(ABNORMAL_RESOURCE_ACCESS_OBJECT_LOGINDATE));
        }
        request.getServletContext().setAttribute(ABNORMAL_RESOURCE_ACCESS_KEY, aaMap);
    }

}
