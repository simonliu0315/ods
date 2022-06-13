package gov.sls.ods.io;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.ICsvMapWriter;
import org.supercsv.prefs.CsvPreference;
import org.supercsv.util.Util;

public class OdsCsvMapWriter extends OdsAbstractCsvWriter implements
        ICsvMapWriter {
    private final List<Object> processedColumns = new ArrayList();

    public OdsCsvMapWriter(Writer writer, CsvPreference preference) {
        super(writer, preference);
    }


    @Override
    public void write(Map<String, ?> values, String... nameMapping)
            throws IOException {
        super.incrementRowAndLineNo();
        super.writeRow(Util.filterMapToObjectArray(values, nameMapping));
    }


    public void write(Map<String, ?> values, String[] nameMapping,
            CellProcessor[] processors) throws IOException {
        super.incrementRowAndLineNo();

        Util.executeCellProcessors(this.processedColumns,
                Util.filterMapToList(values, nameMapping), processors,
                getLineNumber(), getRowNumber());

        super.writeRow(this.processedColumns);
    }
}
