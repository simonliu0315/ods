package gov.sls.ods.io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

import org.supercsv.encoder.CsvEncoder;
import org.supercsv.io.ICsvWriter;
import org.supercsv.prefs.CsvPreference;
import org.supercsv.util.CsvContext;
import org.supercsv.util.Util;

public abstract class OdsAbstractCsvWriter implements ICsvWriter {
    private final BufferedWriter writer;
    private final CsvPreference preference;
    private final CsvEncoder encoder;
    private int lineNumber = 0;

    private int rowNumber = 0;

    private int columnNumber = 0;

    public OdsAbstractCsvWriter(Writer writer, CsvPreference preference) {
        if (writer == null)
            throw new NullPointerException("writer should not be null");
        if (preference == null) {
            throw new NullPointerException("preference should not be null");
        }

        this.writer = new BufferedWriter(writer);
        this.preference = preference;
        this.encoder = preference.getEncoder();
    }

    public void close() throws IOException {
        this.writer.close();
    }

    public void flush() throws IOException {
        this.writer.flush();
    }

    /** @deprecated */
    protected String escapeString(String csvElement) {
        CsvContext context = new CsvContext(this.lineNumber, this.rowNumber,
                this.columnNumber);
        String escapedCsv = this.encoder.encode(csvElement, context,
                this.preference);
        this.lineNumber = context.getLineNumber();
        return escapedCsv;
    }

    protected void incrementRowAndLineNo() {
        this.lineNumber += 1;
        this.rowNumber += 1;
    }

    public int getLineNumber() {
        return this.lineNumber;
    }

    public int getRowNumber() {
        return this.rowNumber;
    }

    protected void writeRow(List<?> columns) throws IOException {
        writeRow(Util.objectListToStringArray(columns));
    }

    protected void writeRow(Object[] columns) throws IOException {
        writeRow(Util.objectArrayToStringArray(columns));
    }

    protected void writeRow(String[] columns) throws IOException {
        if (columns == null)
            throw new NullPointerException(String.format(
                    "columns to write should not be null on line %d",
                    new Object[] { Integer.valueOf(this.lineNumber) }));
        if (columns.length == 0) {
            throw new IllegalArgumentException(String.format(
                    "columns to write should not be empty on line %d",
                    new Object[] { Integer.valueOf(this.lineNumber) }));
        }

        //this.writer.write(OdsProcessConstant.FIRST.getToken());
        for (int i = 0; i < columns.length; ++i) {
            this.columnNumber = (i + 1);

            if (i > 0) {
                this.writer.write(OdsProcessConstant.DELIMITER.getToken());
            }

            String csvElement = columns[i];
            if (csvElement != null) {
                this.writer.write(escapeString(csvElement));
            }

        }

        this.writer.write(OdsProcessConstant.LAST.getToken() + this.preference.getEndOfLineSymbols());
    }

    public void writeComment(String comment) throws IOException {
        this.lineNumber += 1;

        if (comment == null) {
            throw new NullPointerException(String.format(
                    "comment to write should not be null on line %d",
                    new Object[] { Integer.valueOf(this.lineNumber) }));
        }

        this.writer.write(comment);
        this.writer.write(this.preference.getEndOfLineSymbols());
    }

    public void writeHeader(String[] header) throws IOException {
        incrementRowAndLineNo();

        writeRow(header);
    }
}
