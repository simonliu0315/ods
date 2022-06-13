package gov.sls.ods.service;

import static org.assertj.core.api.Assertions.assertThat;
import gov.sls.ods.service.BusinessService;
import gov.sls.ods.service.BusinessService.TestData;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;

public class BusinessServiceTest {

    private BusinessService service;

    @Before
    public void setUp() {
        service = new BusinessService();
    }

    @Test
    public void testOneWeekLater() throws Exception {
        String expected = "102/10/02";
        String actual = service.oneWeekLater("102/09/25");

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testComplexNumberCacluation() throws Exception {
        BigDecimal expected = BigDecimal.TEN;

        TestData data = new TestData();
        data.setA(BigDecimal.TEN);

        BigDecimal actual = service.complexNumberCacluation(data);

        assertThat(actual).isEqualTo(expected);
    }
}
