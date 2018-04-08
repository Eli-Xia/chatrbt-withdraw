var Menage = new Vue({
    el: "#menage_account",
    data: {
        list: [],
        wxPubNickname: "",
        imgUrl:base+"/res/portal/images/default.png"
    },
    watch:{
        wxPubNickname:function (newval,oldval) {
            if(newval==""){
                this.queryList();
            }
        }
    },
    created: function () {
        this.queryList();
    },
    methods: {
        queryList: function () {
            var _self = this;
            var data = {};
            if(_self.wxPubNickname.trim() !=''){
                data.wxPubNickname = _self.wxPubNickname;
            }
            $mks.syncJsonPost({
                url: apibase + "/wx-pub/list",
                data: data,
                success_func:function(res){
                    if (res.retCode == RetCode.SUCCESS) {
                        _self.list=res.result;
                    }else{
                        dialog(res.retMsg,2);
                    }
                    $("#layer").hide();
                }
            })
        },
        accountDetail:function (key) {
            wxPubOriginId=key;
            loadMainContent(base+"/account_detail.html")
        }
    }
})