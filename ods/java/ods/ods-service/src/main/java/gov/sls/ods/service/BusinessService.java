package gov.sls.ods.service;

import static java.util.Objects.requireNonNull;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.threeten.bp.LocalDate;

import com.cht.commons.base.ObjectEnhancers;
import com.cht.commons.time.LocalDates;

/**
 * 展示一些業務處理的東西。
 */
@Service
public class BusinessService {

    /**
     * 日期時間處理。
     * <p>
     * 取得七天後的日期。
     * 
     * @param minguoDate
     *            民國年字串，格式是 {@code yyy/MM/dd}。
     * @return 七天後的民國年字串。
     */
    public String oneWeekLater(String minguoDate) {
        requireNonNull(minguoDate, "\"minguoDate\" must not be null.");

        LocalDate date = LocalDates.fromMinguoString(minguoDate, "yyy/MM/dd");
        date = date.plusWeeks(1);

        return LocalDates.toMinguoString(date, "yyy/MM/dd");
    }

    /**
     * {@code TestData} 裡所有欄位的加總，如果其值為 {@code null} 則以 {@code 0} 取代。
     * 
     * @param data
     *            測試資料。
     * @return 資料內欄位的加總。
     */
    public BigDecimal complexNumberCacluation(TestData data) {
        requireNonNull(data, "\"data\" must not be null.");

        // 不要再一欄一欄設 BigDecimal.ZERO 了!!!

        // 透過 ObjectEnhancers.returnDefault() 讓任何物件的 getter 都不會回傳 null
        // (只對 Integer, Boolean, Long, Float, BigDecimal, BigInteger, String 等欄位有用)
        TestData dataWithDefaults = ObjectEnhancers.returnDefault(data);

        // 實際的運算
        BigDecimal result = dataWithDefaults.getA().add(dataWithDefaults.getB())
                .add(dataWithDefaults.getC());

        return result;
    }

    /**
     * 測試物件。
     */
    public static class TestData {

        private BigDecimal a;
        private BigDecimal b;
        private BigDecimal c;

        public BigDecimal getA() {
            return a;
        }

        public void setA(BigDecimal a) {
            this.a = a;
        }

        public BigDecimal getB() {
            return b;
        }

        public void setB(BigDecimal b) {
            this.b = b;
        }

        public BigDecimal getC() {
            return c;
        }

        public void setC(BigDecimal c) {
            this.c = c;
        }
    }
}
