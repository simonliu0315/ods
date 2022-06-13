package gov.sls.ods.web.rest;

import java.net.URLEncoder;

import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.web.client.RestTemplate;

@Slf4j
public class ApiTest extends Mockito {

//    @Test
//    public void testODS355I() throws Exception {
//        //String uri = "http://localhost:8080/ods-main/ODS355I/rest/getStat";
//        String uri = "http://192.168.31.151:8080/ods-main/ODS355I/rest/getStat";
//
//        RestTemplate template = new RestTemplate();
//
//        Ods355iDto result = template.getForObject(uri, Ods355iDto.class);
//        
//        System.out.println("Code:" + result.getCode());
//        for (PortalStat st : result.getPortalStatList()) {
//            System.out.println("name(統計名稱):" + st.getName());
//            System.out.println("number(統計數字):" + st.getNumber() + st.getUnit());
//            System.out.println("endDate(統計截止日期):" + st.getEndDate());
//        }
//    }
//    
//    @Test
//    public void testODS354I() throws Exception {
////        DateFormat df = DateFormat.getDateInstance();
////        Date preDate = df.parse("2014/06/01");
//        //Date format: yyyyMMddHHmmss
//        String preDate = "20140601235959";
//        String uri = "http://192.168.31.151:8080/ods-main/ODS354I/rest/getEPaper/" + preDate;
//        
//        RestTemplate template = new RestTemplate();
//
//        Ods354iDto result = template.getForObject(uri, Ods354iDto.class);
//        
//        System.out.println("Code:" + result.getCode());
//        for (EPaper ep : result.getEPaperList()) {
//            System.out.println("name(主題名稱):" + ep.getName());
//            System.out.println("description(主題說明):" + ep.getDescription());
//            System.out.println("imageUrl(圖片):" + ep.getImageUrl());
//        }
//    }
//    
//    @Test
//    public void testODS356I() throws Exception {
//        String uri = "http://localhost:8080/ods-main/ODS356I/rest/getRss";
//        
//        RestTemplate template = new RestTemplate();
//
//        Ods356iDto result = template.getForObject(uri, Ods356iDto.class);
//        
//        System.out.println("Code:" + result.getCode());
//        for (RSSItem rss : result.getRssItemList()) {
//            System.out.println("title(主題名稱):" + rss.getTitle());
//            System.out.println("description(主題說明):" + rss.getDescription());
//            System.out.println("link(主題網址):" + rss.getLink());
//        }
//    }
    
    @Test
    public void testODS358I() throws Exception {
        //TEST findPackages API
        //String uri = "http://localhost:8080/ods-main/api/packages";
        String text = URLEncoder.encode("趙", "UTF-8");
        String uri = "http://localhost:8080/ods-main/api/packages?packageName="+text+"&resourceFileExts=pdf,image";
        //String uri = "http://192.168.31.151:8080/ods-main/ODS355I/rest/getStat";

        RestTemplate template = new RestTemplate();

        //TEST GET METHOD
        JSONObject packages = template.getForObject(uri, JSONObject.class);
        
        log.info("packages:" + packages);
                
        //TEST POST METHOD
//        
//        HttpServletRequest request = mock(HttpServletRequest.class);    
//
//        when(request.getParameter("packageName")).thenReturn("testType123");
//        when(request.getParameter("orderByType")).thenReturn("0");
//        when(request.getParameter("packageTags")).thenReturn(null);
//        when(request.getParameter("resourceFileExts")).thenReturn(null);
        
//        Ods358iDto dto = new Ods358iDto();
//        dto.setPackageName("testType123");
//
//        JSONObject resultPost = template.postForObject(uri, dto, JSONObject.class);
//        
//        log.info("JSONObject POST:" + resultPost);
        
        
//        //TEST findPackageVersions API
//        uri = "http://localhost:8080/ods-main/api/package/versions/7F306192-947E-4659-8779-A98CA62A3C7F";
//        JSONObject packageVersions = template.getForObject(uri, JSONObject.class);
//        
//        log.info("packageVersions:" + packageVersions);
//        
//        //TEST findPackageResources API
//        uri = "http://localhost:8080/ods-main/api/resources/7F306192-947E-4659-8779-A98CA62A3C7F/5";
//        JSONObject packageResources = template.getForObject(uri, JSONObject.class);
//        
//        log.info("packageResources:" + packageResources);
//        
//        //TEST findResourceDownloadList API
//        uri = "http://localhost:8080/ods-main/api/resourceDownloadList/7F306192-947E-4659-8779-A98CA62A3C7F/5";
//        JSONObject resourceDownloadList = template.getForObject(uri, JSONObject.class);
//        
//        log.info("resourceDownloadList:" + resourceDownloadList);
    }

}
