package gov.sls.ods.dto;

import gov.sls.ods.dto.PlainDto;

import org.junit.Test;

public class PlainDtoTest {

    @Test
    public void testLombok() throws Exception {
        PlainDto dto = new PlainDto();

        // 我們並沒有 PlainDto 裡寫 getName()，但是神奇的事已經發生了....
        dto.getName();
        //  ^^^^^^^ 如果這裡出現 Error (The method getName() is undefined for the type PlainDto)
        // 請到 http://210.61.225.102/projects/psr-sdp/wiki
        // 下載新版的 Cultivator;
        // 或是到 http://projectlombok.org/ 下載 lombok.jar 安裝
    }
}
