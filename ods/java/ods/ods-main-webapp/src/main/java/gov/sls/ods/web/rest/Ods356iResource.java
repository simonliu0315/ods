package gov.sls.ods.web.rest;

import gov.sls.commons.web.rss.RssChannel;
import gov.sls.commons.web.rss.RssGenerator;
import gov.sls.commons.web.rss.RssItem;
import gov.sls.entity.ods.OdsPackage;
import gov.sls.entity.ods.OdsPackageVersion;
import gov.sls.ods.dto.RSSItem;
import gov.sls.ods.service.Ods356iService;
import gov.sls.ods.web.dto.Ods356iDto;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cht.commons.time.Instants;
import com.cht.commons.time.LocalDateTimes;
import com.google.common.base.Strings;

@Slf4j
@Controller
@RequestMapping("ODS356I/rest")
public class Ods356iResource {
    
    @Autowired
    private Ods356iService service;

    /*
     * 使用execute的方式執行，傳入entity在判斷要查詢單筆或是多筆
     * 要注意網址不要與key值的網址重覆
     */
    @RequestMapping(value = "/getRss", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Ods356iDto getRss() {
        Ods356iDto dto = new Ods356iDto();
        List<RSSItem> rssItemList = new ArrayList<RSSItem>();
        try {
            rssItemList = service.getRss();
            if (rssItemList.isEmpty()) {
                dto.setCode("ODS01");
            } else {
                dto.setCode("ODS00");
            }
        } catch (Exception e) {
            dto.setCode("ODS99");
        }        
        dto.setRssItemList(rssItemList);
        
        return dto;
    }

    
    @RequestMapping(value = "/rss/", 
            method=RequestMethod.GET, produces = MediaType.TEXT_XML_VALUE + ";charset=UTF-8") //重要必加utf-8，不然會是亂碼
    public @ResponseBody String rssChannel(HttpServletRequest request) {
            String siteUrl = getSiteUrl(request);
            
            // 建立一個RSS頻道
            RssChannel rssChannel = new RssChannel();
            rssChannel.setTitle(service.findChannelTitle(
                RssGenerator.RSS_CHANNEL_TITLE)); //設定頻道的標題
            rssChannel.setLink(siteUrl); //設定網站的URL，我是直接從URL parse，ex. http://www.einvoice.com.nat.gov.tw/ods-main/
            rssChannel.setDescription(RssGenerator.RSS_CHANNEL_DESCRIPTION); //設定頻道描述

            // 將自己搜尋出來的頻道內容填成RssItem
            List<OdsPackageVersion> contents = service.getRssData(); 
            List<RssItem> rssItems = new ArrayList<RssItem>(contents.size());
            for (OdsPackageVersion content : contents) {
                RssItem rssItem = new RssItem();
                rssItem.setTitle(content.getName()); //項目標題
                rssItem.setLink(siteUrl + "ODS303E" + "/" + content.getPackageId() + "/" + content.getVer() + "/"); //項目連結URL
                rssItem.setDescription(content.getDescription());

                if (content.getCreated() != null) {
                    rssItem.setPublishedDate(LocalDateTimes.toString( //有發佈日期就填發佈日期
                        LocalDateTimes.from(Instants.toDate(
                            Instants.from(content.getCreated()))), "yyyy-MM-dd"));    
                }
                //rssItem.setAuthor(); //作者，備用
                //rssItem.setCategoryTheme(data.get("categoryTheme")); //主題，備用
                //rssItem.setCategoryCake(data.get("categoryCake")); //施政，備用
                //rssItem.setCategoryService(data.get("categoryService")); //服務，備用
                rssItems.add(rssItem);
            }
            
            return RssGenerator.generateRss(rssChannel, rssItems);
        }
    
    public String getSiteUrl(HttpServletRequest request) {
        String url = request.getRequestURL().toString(); 
        String[] urls = url.split("/");
        if (urls.length > 3) {
            return (urls[0] + "/" + urls[1] + "/" + urls[2] + "/" + urls[3] + "/");
        } else {
            return "";
        }
    }

}
