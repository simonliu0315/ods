package gov.sls.ods.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.DateCellProcessor;
import org.supercsv.io.ICsvMapWriter;
import org.supercsv.prefs.CsvPreference;


/**
 * <pre>
 * ods 轉檔類別
 * 使用方式：
 * String filePath = "D:/sls-workspace/testCode/outputdir/DB.TABLE.20150101.080000.TXT_TMP";
 * 
 * '//addField是有順序性的
 * '//processorType目前只有date、datetime，其他型態不用加processor
 * OdsToCsv odsToCsv = new OdsToCsv.Builder().hasHeader(true)
 *   .addField("paraDate", OdsCellProcessorType.DATE)
 *   .addField("paraBigDec")
 *   .addField("paraStr")
 *   .addField("paraDate", OdsCellProcessorType.DATETIME).build();
 * odsToCsv.beanListToCsv(testlist, filePath);
 * </pre>
 */
@Slf4j
public class OdsToCsv {
    /**
     * 顯示表頭註記
     */
    private boolean hasHeader = false;
    /**
     * 處理器
     */
    private List<CellProcessor> processors ;
    /**
     * 欄位名稱
     */
    private List<String> header ;

    /**取得處理器陣列
     * @return CellProcessor[]
     */
    public CellProcessor[] getProcessors() {
        if (null != this.processors){
            return this.processors.toArray(new CellProcessor[processors.size()]);
        }
        return null;
    }

    /**取得表頭欄位名稱陣列
     * @return String[]
     */
    public String[] getHeader() {
        if (null != this.header){
            return this.header.toArray(new String[header.size()]);
        }
        return null;
    }

    /**取得 顯示表頭註記 true表示顯示表頭，false表示不顯示
     * @return boolean
     */
    public boolean isHasHeader() {
        return hasHeader;
    }

    /**private 建構子
     * @param builder
     */
    private OdsToCsv(Builder builder) {
        this.processors = builder.processors;
        this.header = builder.header;
        this.hasHeader = builder.hasHeader;
    }


    /**
     * from beanlist to csv file with header
     * 
     * @param list
     *            source bean list
     * @param outputFilePath
     *            output full path
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException
     * @throws IOException
     */
    public void beanListToCsv(List<Map<String, Object>> list, String outputFilePath)
            throws FileNotFoundException, UnsupportedEncodingException,
            IOException {
        
        // settting preference with OdsCsvEncode
        // line first is |#
        // line last is #/
        // delimiter is *|
        // no quote
        // no convert delimiter、quote、\r\n
        log.debug("beanListToCsv csvPreference building");
        final CsvPreference csvPreference = new CsvPreference.Builder('"', '|',
                "\n").useEncoder(new OdsCsvEncoder()).build();

        ICsvMapWriter mapWriter = null;
        try {
            File toFile = new File(outputFilePath);
            OutputStream os = new FileOutputStream(toFile);
            /*
             * byte[] UTF8_BOM = {(byte)0xEF, (byte)0xBB, (byte)0xBF};
             * os.write(UTF8_BOM); os.flush();
             */
            OutputStreamWriter out = new OutputStreamWriter(os, "UTF-8");
            mapWriter = new OdsCsvMapWriter(out, csvPreference);
            if (isHasHeader()) {
                log.debug("beanListToCsv writeHeader " + isHasHeader());
                mapWriter.writeHeader(getHeader());
            }
            log.debug("beanListToCsv writing... list size:" + list.size());
            for (Map<String, Object> pojotest : list) {
                mapWriter.write(pojotest, getHeader(), getProcessors());
            }
        } finally {
            if (mapWriter != null) {
                mapWriter.close();
            }
            log.debug("beanListToCsv close.");
        }
    }
    
    
    
    /**
     * builder產生
     */
    public static class Builder {
        private List<CellProcessor> processors ;
        private List<String> header ;
        private boolean hasHeader = false;

        /**
         * 
         */
        public Builder(){
            this.processors = new ArrayList<CellProcessor>();
            this.header = new ArrayList<String>();
        }
        
        /**顯示表頭註記
         * @param hasHeader true表示顯示表頭，false表示不顯示 
         */
        public Builder(boolean hasHeader){
            this.processors = new ArrayList<CellProcessor>();
            this.header = new ArrayList<String>();
            this.hasHeader = hasHeader;
        }
        
        /**
         * @param headName 欄位名稱
         * @param type 欄位型態：目前只支援date 、datetime
         * @return
         */
        public Builder addField(String headName, OdsCellProcessorType type) {
            this.header.add(headName);
            this.processors.add(type.getCellProcessor());
            return this;
        }
        
        /**
         * @param headName 欄位名稱
         * @return
         */
        public Builder addField(String headName) {
            this.header.add(headName);
            this.processors.add(null);
            return this;
        }

        /** true表示顯示表頭，false表示不顯示 
         * @param hasHeader 顯示表頭註記
         * @return
         */
        public Builder hasHeader(boolean hasHeader) {
            this.hasHeader = hasHeader;
            return this;
        }
        
        /**建立OdsToCsv
         * @return OdsToCsv
         */
        public OdsToCsv build() {
            return new OdsToCsv(this);
        }
    }
    
    /**
     * processorType目前只有 DATE,DATETIME，其他型態不用加processor
     */
    public enum OdsCellProcessorType {
        DATE(new OdsFmtDate("yyyyMMddHHmmss")),
        DATETIME( new OdsFmtDate("yyyyMMddHHmmssSSS000"));
        
        private DateCellProcessor cellProcessor;
        
        OdsCellProcessorType(DateCellProcessor cellProcessor){
            this.cellProcessor = cellProcessor;
        }

        public DateCellProcessor getCellProcessor() {
            return this.cellProcessor;
        }	
    }
}
