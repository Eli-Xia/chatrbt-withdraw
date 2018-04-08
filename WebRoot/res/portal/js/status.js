//查询登录状态
var queryStatus = function() {
    $mks.syncJsonPost({
        url: apibase+'/user/info/query',
        success_func: function(res){
            if (res.retCode == RetCode.SUCCESS) {
                // _self.avatar = res.result.avatar;
                return
            } else {
                location.replace(base + '/index.html');
            }
        }
    });
}
queryStatus();