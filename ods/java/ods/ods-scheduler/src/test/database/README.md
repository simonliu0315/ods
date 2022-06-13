資料庫測試檔目錄
================

eclipse 開發環境說明
--------------------　
將 test-datasource.xml.template-oracle 或 test-datasource.xml.template-sqlserver 檔案複製並更名
test-datasource.xml 即可讓應用程式連線到乙測的 Oracle 11g 資料庫或是 Microsoft SQL Server。

如果沒有任何 test-datasource.xml，預設會在記憶體內建立 HSQL 資料庫，並試著載入 init.sql 初始化，
再由 Entity Class 自動產生 DDL 並自動更新 HSQL 資料庫 Schema 內容，方便離線開發。在此模式下，
建議盡量不要用到單一資料庫系統專用的 SQL 語法。

以上功能只有在 eclipse 開發環境中才會生效，不用擔心會弄爛正式環境。
