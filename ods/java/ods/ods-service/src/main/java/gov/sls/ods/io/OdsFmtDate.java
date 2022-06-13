/**
 * 
 */
package gov.sls.ods.io;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.extern.slf4j.Slf4j;

import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.DateCellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.util.CsvContext;

/**
 * 
 */
@Slf4j
public class OdsFmtDate extends CellProcessorAdaptor implements DateCellProcessor{

    private final String dateFormat;

    public OdsFmtDate(String dateFormat) {
        checkPreconditions(dateFormat);
        this.dateFormat = dateFormat;
    }

    public OdsFmtDate(String dateFormat, StringCellProcessor next) {
        super(next);
        checkPreconditions(dateFormat);
        this.dateFormat = dateFormat;
    }

    private static void checkPreconditions(String dateFormat) {
        if (dateFormat == null)
            throw new NullPointerException("dateFormat should not be null");
    }

    public Object execute(Object value, CsvContext context) {
        if (null==value){
            return this.next.execute(value, context);
        }

        SimpleDateFormat formatter;
        if (value instanceof String) {
            // 2015-05-08 10:20:52.5570000
            //log.debug("OdsFmtDate execute String:" + value);
            formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            try {
                value = formatter.parse(((String)value).substring(0, ((String)value).length()-4));
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
        if (!(value instanceof Date)) {
            throw new SuperCsvCellProcessorException(Date.class, value,
                    context, this);
        }
        try {
            formatter = new SimpleDateFormat(this.dateFormat);
        } catch (IllegalArgumentException e) {
            throw new SuperCsvCellProcessorException(String.format(
                    "'%s' is not a valid date format",
                    new Object[] { this.dateFormat }), context, this, e);

        }
        String result = formatter.format((Date) value);
        return this.next.execute(result, context);
    }
}
