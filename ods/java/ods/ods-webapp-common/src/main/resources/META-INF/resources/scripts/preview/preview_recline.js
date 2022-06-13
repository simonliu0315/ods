// recline preview module
this.ckan.module('reclinepreview', function (jQuery, _) {    
  return {
    options: {
      i18n: {
        errorLoadingPreview: "Could not load preview",
        errorDataProxy: "DataProxy returned an error",
        errorDataStore: "DataStore returned an error",
        previewNotAvailableForDataType: "Preview not available for data type: "
      },
      site_url: ""
    },

    initialize: function () {
      jQuery.proxyAll(this, /_on/);
      this.el.ready(this._onReady);
      // hack to make leaflet use a particular location to look for images
      L.Icon.Default.imagePath = this.options.site_url + 'vendor/leaflet/0.4.4/images';
    },

    _onReady: function() {
        //console.log('_onReady');
      this.loadPreviewDialog(preload_resource);
    },

    // **Public: Loads a data preview**
    //
    // Fetches the preview data object from the link provided and loads the
    // parsed data from the webstore displaying it in the most appropriate
    // manner.
    //
    // link - Preview button.
    //
    // Returns nothing.
    loadPreviewDialog: function (resourceData) {
      var self = this;

      function showError(msg){
        msg = msg || _('error loading preview');
        window.parent.ckan.pubsub.publish('data-viewer-error', msg);
      }

      recline.Backend.DataProxy.timeout = 10000;
      // will no be necessary any more with https://github.com/okfn/recline/pull/345
      recline.Backend.DataProxy.dataproxy_url = '//jsonpdataproxy.appspot.com';
      //console.log(JSON.stringify(resourceData));
      // 2 situations
      // a) something was posted to the datastore - need to check for this
      // b) csv or xls (but not datastore)
      resourceData.formatNormalized = this.normalizeFormat(resourceData.format);

      resourceData.url  = this.normalizeUrl(resourceData.url);
      if (resourceData.formatNormalized === '') {
        var tmp = resourceData.url.split('/');
        tmp = tmp[tmp.length - 1];
        tmp = tmp.split('?'); // query strings
        tmp = tmp[0];
        var ext = tmp.split('.');
        if (ext.length > 1) {
          resourceData.formatNormalized = ext[ext.length-1];
        }
      }

      var errorMsg, dataset;
      
      if (resourceData.datastore_active) {
        resourceData.backend =  'ckan';
        // Set endpoint of the resource to the datastore api (so it can locate
        // CKAN DataStore)
        resourceData.endpoint = jQuery('body').data('site-root') + 'api';
        //console.log("resourceData:" + JSON.stringify(resourceData));
        dataset = new recline.Model.Dataset(resourceData);
        //console.log("dataset:" + JSON.stringify(dataset));
        errorMsg = this.options.i18n.errorLoadingPreview + ': ' + this.options.i18n.errorDataStore;
        dataset.fetch()
          .done(function(dataset){
              self.initializeDataExplorer(dataset);
          })
          .fail(function(error){
            if (error.message) errorMsg += ' (' + error.message + ')';
            showError(errorMsg);
          });        
      } else if (resourceData.formatNormalized in {'csv': '', 'xls': ''}) {
        // set format as this is used by Recline in setting format for DataProxy
        resourceData.format = resourceData.formatNormalized;
        resourceData.backend = 'dataproxy';
        dataset = new recline.Model.Dataset(resourceData);
        //console.log(JSON.stringify(dataset));
        errorMsg = this.options.i18n.errorLoadingPreview + ': ' +this.options.i18n.errorDataProxy;
        //console.log(JSON.stringify(errorMsg));
        dataset.fetch()
          .done(function(dataset){
            dataset.bind('query:fail', function (error) {
              jQuery('.data-view-container', self.el).hide();
              jQuery('.header', self.el).hide();
            });
            //console.log(JSON.stringify(dataset));
            self.initializeDataExplorer(dataset);
          })
          .fail(function(error){
              //console.log(JSON.stringify(error));
            if (error.message) errorMsg += ' (' + error.message + ')';
            showError(errorMsg);
          });
      }
    },

    initializeDataExplorer: function (dataset) {                       
      var views = [
        {
          id: 'grid',
          label: '表格',
          view: new recline.View.SlickGrid({
            model: dataset
          })
        },
        {
          id: 'graph',
          label: '圖形',
          view: new recline.View.Graph({
            model: dataset
//            state: {
//                group: "",
//                series: [""]
//              }
          })
        }/*,
        {
          id: 'map',
          label: '地圖',
          view: new recline.View.Map({
            model: dataset
          })
        }*/
      ];

      var sidebarViews = [
        {
          id: 'valueFilter',
          label: '過濾條件',
          view: new recline.View.ValueFilter({
            model: dataset
          })
        }
      ];

      var dataExplorer = new recline.View.MultiView({
        el: this.el,
        model: dataset,
        views: views,
        sidebarViews: sidebarViews,
        config: {
          readOnly: true
        }
      });

    },
    normalizeFormat: function (format) {
      var out = format.toLowerCase();
      out = out.split('/');
      out = out[out.length-1];
      return out;
    },
    normalizeUrl: function (url) {
      if (url.indexOf('https') === 0) {
        return 'http' + url.slice(5);
      } else {
        return url;
      }
    }
  };
});
