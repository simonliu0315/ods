財政部財政資訊中心「推動電子發票，創造智慧好生活計畫」建置專案範例應用程式
==================

安裝 Oracle 相關函式庫
----------------------

本範例會使用到無法從公開 Maven 儲存庫取得的 Oracle jar 檔，您必需依以下程序手動下載並安裝到本地端的儲存庫:

* 從 Oracle Technology Network 下載 (需要註冊):
http://www.oracle.com/technetwork/database/enterprise-edition/jdbc-112010-090769.html
```
http://download.oracle.com/otn/utilities_drivers/jdbc/11204/ojdbc6.jar
```

* 使用 Maven 指令安裝: 
```
mvn install:install-file -DgroupId=com.oracle.jdbc -DartifactId=oracle.jdbc -Dversion=11.2.0.4 -Dclassifier=java-6 -Dpackaging=jar -Dfile=/path/to/ojdbc6.jar
```
