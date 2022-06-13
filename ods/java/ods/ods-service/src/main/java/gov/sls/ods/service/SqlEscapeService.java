package gov.sls.ods.service;

import org.apache.commons.lang.StringUtils;
//import org.owasp.esapi.ESAPI;
//import org.owasp.esapi.codecs.OracleCodec;
import org.springframework.stereotype.Service;

/**
 * 避免sql injection攻擊機制
 */

@Service
public class SqlEscapeService {

    public String escapeMsSql(String origSql) {
        if (origSql == null) {
            return null;
        }
        
        String newSql = origSql;

        // http://technet.microsoft.com/zh-tw/library/ms161953(v=sql.105).aspx
        newSql = StringUtils.replace(newSql, "'", "''");
        newSql = StringUtils.replace(newSql, ";", "；");
        newSql = StringUtils.replace(newSql, "--", "- -");
        newSql = StringUtils.replace(newSql, "/*", "／＊");
        newSql = StringUtils.replace(newSql, "*/", "＊／");
        newSql = StringUtils.replace(newSql, "xp_", "xp__");
        
        return newSql;
        
        //return ESAPI.encoder().encodeForSQL(new OracleCodec(),origSql);
    }

}
