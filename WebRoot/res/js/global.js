//定义结构
function MKS(){};
//添加公共函数
/**
 * 同步调用ajax http post
 * @param {Object} params
 */
MKS.prototype.syncJsonPost = function(params){

    var jsonData = JSON.stringify(params.data);

    $.ajax({
        type: "POST",
        url: params.url,
        dataType: "json",
        contentType: "application/json;charset=UTF-8",
        data: jsonData,
        async: true,
        success: function(response){
            params.success_func(response);
        }
    });
};

/**
 * 同步调用ajax http post
 * @param {Object} params
 */
MKS.prototype.syncJsonGet = function(params){

    $.ajax({
        type: "GET",
        url: params.url,
        dataType: "json",
        contentType: "application/json;charset=UTF-8",
        async: true,
        success: function(response){
            params.success_func(response);
        }
    });
};


/**
 * 获取当前url中的参数值
 * @param {Object} paramName
 */
MKS.prototype.getUrlParamValue = function(paramName){

    var reg = new RegExp("(^|&)" + paramName + "=([^&]*)(&|$)", "i");

    var r = window.location.search.substr(1).match(reg);
    if (r != null){
        return unescape(r[2]);
    }else{
        return null;
    }
};

/**
 * 日期过滤器  yyyy-mm-dd hh:mm:ss
 * @param {Object} unixtimeMilSec
 */
MKS.prototype.formatUnixTimeStamp = function( ms ){
    var d = new Date(ms);
    return d.getFullYear() + '-' + add_zero(d.getMonth()+1) + '-'
        + add_zero(d.getDate()) + ' ' + add_zero(d.getHours()) + ':'
        + add_zero(d.getMinutes()) + ':' + add_zero(d.getSeconds());

    /**
     * 给日期补零
     */
    function add_zero(temp){

        if (temp < 10){
            return "0" + temp;
        }else{
            return temp;
        }
    }
};
/**
 * 日期过滤器  yyyy-mm-dd
 * @param {Object} DateTime
 */
MKS.prototype.formatDateTime = function( ms ){
    if (ms) {
        var d = new Date(ms);
        return d.getFullYear() + '-' + add_zero(d.getMonth()+1) + '-'
            + add_zero(d.getDate());
    } else {
        return '--'
    }
    /**
     * 给日期补零
     */
    function add_zero(temp){

        if (temp < 10){
            return "0" + temp;
        }else{
            return temp;
        }
    }
};

/**
 * 字符串是否为空
 * @param {Object} str
 */
MKS.prototype.strIsEmpty = function(str){

    if ( str == undefined ){
        return true;
    }

    if ( str == null ){
        return true;
    }

    if ( str.trim() == '' ){
        return true;
    }

    return false;
}


//实例化
$mks = new MKS();

