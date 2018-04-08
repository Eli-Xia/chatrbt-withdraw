var pushMessageConfig = new Vue({
    el: '#wx_push_config',
    data: {
        ratio: '',
        adSwitch: '',
        adId: '',
        list: [],
        left: "left",
        configs: {
            pushAdRatio: "",
            pushAdId: "",
            pushAdSwitch: "",
            chatPushAdCount: ""
        },
        alterActive: false
    },
    watch: {
        ratio: function (newval, oldval) {
            if (newval > 100) {
                this.ratio = 100;
                return;
            } else if (newval < 0) {
                this.ratio = 0;
                return;
            }
            if (oldval != '') {
                this.configs.pushAdRatio = newval / 100;
            }
        },
        adSwitch: function (newval, oldval) {
            if (oldval === '') {
            } else {
                this.configs.pushAdSwitch = newval == true ? 1 : 0;
            }
        },
        configs: {
            handler: function (newValue, oldValue) {
                if (oldValue.pushAdRatio != '') {
                    this.alterActive = true;
                }
                if (newValue.chatPushAdCount < 0 || newValue.chatPushAdCount == '') {
                    newValue.chatPushAdCount = 1
                }
            },
            deep: true
        },
    },
    created: function () {
        var _self = this;
        $.extend(SaimiriPagination.defaults, {curPage: 1, total: 0, pageSize: 20});
        // _self.queryList(1);
        _self.adConfig();
        $("#page").on("click", "button", function (e) {
            var text = e.target.innerHTML;
            if (Number(text)) {
                if (SaimiriPagination.defaults.curPage != Number(text)) {
                    _self.queryList(Number(text));
                }
            }
        })
    },
    methods: {
        /**
         * 获取广告推送配置
         */
        adConfig: function () {
            var _self = this;
            $mks.syncJsonPost({
                url: apibase + "/admin/push-message/config/ad/list",
                success_func: function (resp) {
                    if (resp.retCode == RetCode.SUCCESS) {
                        _self.configs = resp.result;
                        _self.ratio = resp.result.pushAdRatio * 100;
                        _self.adId = resp.result.pushAdId;
                        _self.adSwitch = resp.result.pushAdSwitch == 1 ? true : false;
                    } else {
                        alert(resp.retMsg);
                        if (resp.retCode == RetCode.NO_LOGIN) {
                            location.href = base + '/admin/login.html';
                        }
                    }
                }
            })
        },
        /**
         *获取列表
         */
        queryList: function (page) {
            var _self = this;
            var page = page;
            $mks.syncJsonPost({
                url: apibase + "/admin/ad/list",
                data: {
                    page: page,
                    pageSize: SaimiriPagination.defaults.pageSize
                },
                success_func: function (resp) {
                    if (resp.retCode == RetCode.SUCCESS) {
                        _self.list = resp.result;
                        $.extend(SaimiriPagination.defaults, {
                            curPage: page,
                            total: resp.total,
                            pageCount: Math.ceil(resp.total / SaimiriPagination.defaults.pageSize)
                        });
                        $("#page").html(SaimiriPagination.paging());
                    } else {
                        alert(resp.retMsg);
                        if (resp.retCode == RetCode.NO_LOGIN) {
                            location.href = base + '/admin/login.html';
                        }
                    }
                }
            })
        },
        /**
         * 提交 配置
         */
        configSum: function () {
            var _self = this;
            if (this.alterActive == false) {
                return;
            }
            $mks.syncJsonPost({
                url: apibase + "/admin/push-message/config/ad/update",
                data: this.configs,
                success_func: function (resp) {
                    if (resp.retCode == RetCode.SUCCESS) {
                        alert("修改成功!");
                        _self.alterActive = false;
                    } else {
                        alert(resp.retMsg);
                        if (resp.retCode == RetCode.NO_LOGIN) {
                            location.href = base + '/admin/login.html';
                        }
                    }
                }
            })
        },
        advertisingModel: function () {
            this.queryList(1);
            $('#ad_select').modal('show');
        },
        /**
         * 选择广告
         */
        adSelect: function () {
            this.configs.pushAdId = this.adId;
            $('#ad_select').modal('hide');
        },
        cancelSelect: function () {
            this.adId = this.configs.pushAdId;
            $('#ad_select').modal('hide');
        },
        /**
         * 向前翻页
         */
        lastPage: function () {
            if (SaimiriPagination.defaults.curPage - 1 > 0) {
                SaimiriPagination.defaults.curPage -= 1;
                this.queryList(SaimiriPagination.defaults.curPage);
            }
        },
        /**
         * 向后翻页
         */
        nextPage: function () {
            if (SaimiriPagination.defaults.curPage + 1 <= SaimiriPagination.defaults.pageCount) {
                SaimiriPagination.defaults.curPage += 1;
                this.queryList(SaimiriPagination.defaults.curPage);
            }
        }
    },
    filters: {
        formateTimeStamp: function (time) {
            return $mks.formatUnixTimeStamp(time);
        },
        // 广告类型
        formateAdType: function (type) {
            if (type == 1) {
                return "图文类型"
            } else if (type == 2) {
                return "文本类型"
            } else {
                return "图片类型"
            }
        },
        //推送类型
        formatePushType: function (type) {
            if (type == 1) {
                return "聊天时推送"
            } else if (type == 2) {
                return "48小时推送"
            }
        },
    }
});