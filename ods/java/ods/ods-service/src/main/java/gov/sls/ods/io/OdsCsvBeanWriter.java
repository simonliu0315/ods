package gov.sls.ods.io;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCsvReflectionException;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;
import org.supercsv.util.MethodCache;
import org.supercsv.util.Util;

public class OdsCsvBeanWriter extends OdsAbstractCsvWriter implements
        ICsvBeanWriter {
    private final List<Object> beanValues = new ArrayList();

    private final List<Object> processedColumns = new ArrayList();

    private final MethodCache cache = new MethodCache();

    public OdsCsvBeanWriter(Writer writer, CsvPreference preference) {
        super(writer, preference);
    }

    private void extractBeanValues(Object source, String[] nameMapping) {
        if (source == null)
            throw new NullPointerException(
                    "the bean to write should not be null");
        if (nameMapping == null) {
            throw new NullPointerException(
                    "the nameMapping array can't be null as it's used to map from fields to columns");

        }

        this.beanValues.clear();

        for (int i = 0; i < nameMapping.length; ++i) {
            String fieldName = nameMapping[i];

            if (fieldName == null) {
                this.beanValues.add(null);
            } else {
                Method getMethod = this.cache.getGetMethod(source, fieldName);
                try {
                    this.beanValues
                            .add(getMethod.invoke(source, new Object[0]));
                } catch (Exception e) {
                    throw new SuperCsvReflectionException(String.format(
                            "error extracting bean value for field %s",
                            new Object[] { fieldName }), e);
                }
            }
        }
    }

    public void write(Object source, String[] nameMapping) throws IOException {
        super.incrementRowAndLineNo();

        extractBeanValues(source, nameMapping);

        super.writeRow(this.beanValues);
    }

    public void write(Object source, String[] nameMapping,
            CellProcessor[] processors) throws IOException {
        super.incrementRowAndLineNo();

        extractBeanValues(source, nameMapping);

        Util.executeCellProcessors(this.processedColumns, this.beanValues,
                processors, getLineNumber(), getRowNumber());

        super.writeRow(this.processedColumns);
    }
}
