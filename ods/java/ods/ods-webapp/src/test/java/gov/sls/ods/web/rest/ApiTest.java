package gov.sls.ods.web.rest;

import gov.sls.ods.dto.PackageInfo;
import gov.sls.ods.web.dto.Ods751iDto;

import org.junit.Test;
import org.springframework.web.client.RestTemplate;

public class ApiTest {

    @Test
    public void testODS751I() throws Exception {
        String uri = "http://192.168.31.151:8080/ods/ODS751I/rest/getPackageInfo";
        //String uri = "http://localhost:8080/ods/ODS751I/rest/getPackageInfo";
        
        
        RestTemplate template = new RestTemplate();

        Ods751iDto result = template.getForObject(uri, Ods751iDto.class);
        
        System.out.println("Code:" + result.getCode());
        for (PackageInfo packageInfo : result.getPackageInfoList()) {
            System.out.println("packageId(主題ID):" + packageInfo.getPackageId());
            System.out.println("packageName(主題名稱):" + packageInfo.getPackageName());
        }
    }

}
