
      // 主題文件檔案
      function showPackageDocument() {
          var formData = {packageId : $("#packageId").val()};
          
          //RESTFul uri對應參數
          var param = {};

          url = "/" + parseUrl().context + "/ODS315E/findPackageDocument";

          var promise = chtAjaxRest.find(url, formData, param);

          // success處理
          promise.done(function(data) {
              var html='<table border="1" width="100%">';
              $.each(data.data, function( index, value ) {
                  html+='<tr><td>'+value.description+'</td><td>'+value.filename+'</td><td width="60px"><button onclick="downloadPackageDocument(\''+value.packageId+'\',\''+value.id+'\')">下載</button></td></tr>';
              });
              html+='</table>';
              bootbox.dialog({
                  message : html,
                  title : "主題文件下載",
                  buttons : {
                      close : {
                          label : "關閉",
                          className : "btn-danger' title='關閉'",
                          callback : function() {
                              return;
                          }
                      },
                      dowloadAll : {
                          label : "打包下載",
                          className : "btn-danger' title='打包下載'",
                          callback : function() {
                              downloadPackageDocumentPack();
                          }
                      }
                  }
              });
          });
          // error處理
          promise.fail(function(xhrInstance, status, xhrException) {
              bootbox.alert("fail:" + status + " Message:" + xhrException);
          });
      };
      
      // 下載單一主題文件檔案
      function downloadPackageDocument(packageId,documentId) {
          window.open("/" + parseUrl().context + "/ODS315E/downloadPackageDocument/" + packageId + "/" + documentId, "newwindow");
      }
      
      // 打包下載主題文件檔案
      function downloadPackageDocumentPack() {
          window.open("/" + parseUrl().context + "/ODS315E/downloadPackageDocumentPack/" + $("#packageId").val(), "newwindow");
      }
      
