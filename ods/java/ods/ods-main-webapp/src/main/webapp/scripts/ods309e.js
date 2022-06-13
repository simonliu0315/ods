//文件載入完成後執行
$(document).ready(function() {
    $("#myNav").affix({
        offset: { 
            top: 0 
        }
    });
    
    $('.fancybox').fancybox({
        closeClick : true
    });
    
//    $('.fancybox').fancybox({
//        padding : 0,
//        openEffect  : 'elastic'
//    });
});
