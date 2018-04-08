var adMgrVue = new Vue({
    el: '#ad_mgr',
    data: {
        list: [],
        arr: {
            adType: 1,
            pushType: 1
        },
        portalImg: null,
        pushImg: null,
        income: null,
        clickAmount: null,
        adId: '',
        putAllList: {},
        tagsAllList: [],
        //关联标签  ID , name列表
        ofTagsList: [],
        //关联标签  ID 列表
        tagIds: [],
        //active  标签name
        activeTagName: '',
        activeTagId: null,
        //所有关联  标签的 所有公众号 ID合集
        allAccountsOfTags: [],
        wxPubs: [],
        wxPubsList: [],
        wxPubIds: [],
        allActive: false,
        queryKey: '',
        // 分页
        pages: 0,
        page: 1,//当前页
    },
    computed: {
        maxIndex: function () {
            return this.page * 12
        },
        minIndex: function () {
            return (this.page - 1) * 12
        }
    },
    watch: {
        income: function (newval, oldval) {
            if (newval < 0) {
                this.income = 0;
            }
            this.income = Number(newval).toFixed(2);
        },
        clickAmount: function (newval, oldval) {
            if (newval < 0) {
                this.clickAmount = 0;
            }
            this.clickAmount = parseInt(this.clickAmount)
        },
        queryKey: function (newval, oldval) {
            if ($.trim(newval) == '') {
                this.activeTag(this.activeTagId, this.activeTagName)
            }
        },
        arr: {
            handler: function (newValue, oldValue) {
                if (newValue.clickAmount < 0) {
                    this.arr.clickAmount = 0;
                }
                this.arr.clickAmount = parseInt(this.arr.clickAmount)
            },
            deep: true
        },
    },
    created: function () {
        var _self = this;
        $.extend(SaimiriPagination.defaults, {curPage: 1, total: 0, pageSize: 10});
        _self.queryList(1);
        $("#page").on("click", "button", function (e) {
            var text = e.target.innerHTML;
            if (Number(text)) {
                if (SaimiriPagination.defaults.curPage != Number(text)) {
                    _self.queryList(Number(text));
                }
            }
        })
        $('#ad_mgr').on('click', '.web-page button', function (e) {
            var text = e.target.innerHTML;
            if (Number(text)) {
                if (_self.page != Number(text)) {
                    _self.page = Number(text);
                    _self.updateList()
                }
            }
        })
        $(function () {
            $('#portal_file').on('change', function () {
                var reader = new FileReader();
                reader.onload = function (e) {
                    _self.portalImg = e.target.result;
                }
                var file = this.files[0].type;
                var i = file.indexOf("/");
                var format = file.slice(0, i);
                if (format == "image") {
                    reader.readAsDataURL(this.files[0]);
                } else {
                    alert("请上传正确的图片格式!")
                }
            });
            $('#push_file').on('change', function () {
                var reader = new FileReader();
                reader.onload = function (e) {
                    _self.pushImg = e.target.result;
                }
                var file = this.files[0].type;
                var i = file.indexOf("/");
                var format = file.slice(0, i);
                if (format == "image") {
                    reader.readAsDataURL(this.files[0]);
                } else {
                    alert("请上传正确的图片格式!")
                }
            });
            $('#alter_portal').on('change', function () {
                var reader = new FileReader();
                reader.onload = function (e) {
                    _self.arr.coverPic = e.target.result;
                }
                var file = this.files[0].type;
                var i = file.indexOf("/");
                var format = file.slice(0, i);
                if (format == "image") {
                    reader.readAsDataURL(this.files[0]);
                } else {
                    alert("请上传正确的图片格式!")
                }
            });
            $('#alter_pic').on('change', function () {
                var reader = new FileReader();
                reader.onload = function (e) {
                    _self.arr.picUrl = e.target.result;
                }
                var file = this.files[0].type;
                var i = file.indexOf("/");
                var format = file.slice(0, i);
                if (format == "image") {
                    reader.readAsDataURL(this.files[0]);
                } else {
                    alert("请上传正确的图片格式!")
                }
            });
        })
        var date = new Date().getTime() + 86400000;
        this.arr.pushTime = $mks.formatDateTime(date);
        $('#push_time').daterangepicker({
            timePicker: false,
            singleDatePicker: true,
            'autoApply': true,
            minDate: $mks.formatDateTime(date),
            range: false,
            'parentEl': "#ad_add .chart-query>div",
            "locale": {
                format: 'YYYY-MM-DD',
            }
        });
        $('#alter_time').daterangepicker({
            timePicker: false,
            singleDatePicker: true,
            'autoApply': true,
            minDate: $mks.formatDateTime(date),
            range: false,
            'parentEl': "#ad_redact .chart-query>div",
            "locale": {
                format: 'YYYY-MM-DD',
            }
        });
        $('#push').daterangepicker({
            timePicker: false,
            singleDatePicker: true,
            'autoApply': true,
            minDate: $mks.formatDateTime(date),
            range: false,
            'parentEl': ".push .chart-query>div",
            "locale": {
                format: 'YYYY-MM-DD',
            }
        });
    },
    methods: {
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
        },
        lastWebPage: function () {
            if (this.page - 1 > 0) {
                this.page -= 1;
                this.updateList()
            }
        },
        nextWebPage: function () {
            if (this.page + 1 <= this.pages) {
                this.page += 1;
                this.updateList()
            }
        },
        /**
         * 获取 其中 一条广告  全部信息
         */
        queryDetail: function (id, el) {
            var _self = this;
            $mks.syncJsonPost({
                url: apibase + "/admin/ad/" + id,
                success_func: function (resp) {
                    if (resp.retCode == RetCode.SUCCESS) {
                        _self.arr = resp.result;
                        _self.arr.prePushTime = $mks.formatDateTime(_self.arr.prePushTime)
                        _self.arr.pushTime = $mks.formatDateTime(_self.arr.pushTime)
                        _self.arr.closeTime = $mks.formatDateTime(_self.arr.closeTime)
                        $('#' + el).modal('show');
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
         * 查看框显示
         */
        modalShow: function (id) {
            this.queryDetail(id, 'ad_check');
        },
        /**
         * 编辑框  显示
         */
        redectShow: function (id, key) {
            $('#ad_check').modal('hide');
            if (key === 1) {
                this.queryDetail(id, 'ad_redact');
            } else {
                $('#ad_redact').modal('show');
            }
            $("#alter_portal").val('')
            $("#alter_pic").val('')
        },
        /**
         * 添加框  显示
         */
        addShow: function () {
            this.$options.methods.int.bind(this)();
            $("#ad_add").modal("show");
        },
        /**
         * 添加 广告
         */
        addAd: function () {
            var _self = this;
            var formData = new FormData();
            formData.append("adType", this.arr.adType);
            formData.append("pushType", this.arr.pushType);
            formData.append("adRecommendStatement", this.arr.adRecommendStatement);
            formData.append("pushTime", this.arr.pushTime);
            var date = new Date().getTime();
            // formData.append("createAt", date);
            if (this.arr.alias) {
                formData.append("alias", this.arr.alias);
            } else {
                alert("请填写广告别名！");
                return;
            }
            if (this.arr.portalTitle) {
                formData.append("portalTitle", this.arr.portalTitle);
            } else {
                alert("请填写展示标题！");
                return;
            }
            if (this.arr.portalContent) {
                formData.append("portalContent", this.arr.portalContent);
            } else {
                alert("请填写展示内容！");
                return;
            }
            if (this.arr.adType == 1 || this.arr.adType == 4) {
                if (this.arr.title) {
                    formData.append("title", this.arr.title);
                } else {
                    alert("请填写广告标题！");
                    return;
                }
                if (this.arr.description) {
                    formData.append("description", this.arr.description);
                } else {
                    alert("请填写广告描述！");
                    return;
                }
                if (this.arr.url) {
                    formData.append("url", this.arr.url);
                } else {
                    alert("请填写url地址！");
                    return;
                }
                if (this.pushImg) {
                    formData.append("wxPic", document.getElementById('push_file').files[0]);
                } else {
                    alert("请上传微信投放图！");
                    return;
                }
            } else if (this.arr.adType == 2) {
                if (this.arr.textContent) {
                    formData.append("textContent", this.arr.textContent);
                } else {
                    alert("请填写文本内容！");
                    return;
                }
            } else {
                if (this.pushImg) {
                    formData.append("wxPic", document.getElementById('push_file').files[0]);
                } else {
                    alert("请上传微信投放图！");
                    return;
                }
            }
            if (this.portalImg) {
                formData.append("coverPic", document.getElementById('portal_file').files[0]);
            } else {
                alert("请上传广告展示图！");
                return;
            }
            if (this.income) {
                formData.append("income", this.income);
            } else {
                alert("请填写点击单价！");
                return;
            }
            if (Number(this.clickAmount)) {
                formData.append("clickAmount", this.clickAmount);
            } else {
                alert("请填写点击次数！");
                return;
            }
            $.ajax({
                url: apibase + "/admin/ad/add",
                data: formData,
                type: 'POST',
                cache: false,
                processData: false,
                contentType: false,
                success: function (resp) {
                    if (resp.retCode == RetCode.SUCCESS) {
                        alert("添加成功!");
                        _self.queryList(1);
                        $("#ad_add").modal("hide");
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
         * 更新 广告
         */
        updateAd: function (id) {
            var _self = this;
            var formData = new FormData();
            formData.append("adType", this.arr.adType);
            formData.append("pushType", this.arr.pushType);
            formData.append("adRecommendStatement", this.arr.adRecommendStatement);
            formData.append("isOpen", this.arr.isOpen);
            if (this.arr.alias) {
                formData.append("alias", this.arr.alias);
            } else {
                alert("请填写广告别名！");
                return;
            }
            if (this.arr.portalTitle) {
                formData.append("portalTitle", this.arr.portalTitle);
            } else {
                alert("请填写展示标题！");
                return;
            }
            if (this.arr.portalContent) {
                formData.append("portalContent", this.arr.portalContent);
            } else {
                alert("请填写展示内容！");
                return;
            }
            if (this.arr.adType == 1 || this.arr.adType == 4) {
                if (this.arr.title) {
                    formData.append("title", this.arr.title);
                } else {
                    alert("请填写广告标题！");
                    return;
                }
                if (this.arr.description) {
                    formData.append("description", this.arr.description);
                } else {
                    alert("请填写广告描述！");
                    return;
                }
                if (this.arr.url) {
                    formData.append("url", this.arr.url);
                } else {
                    alert("请填写url地址！");
                    return;
                }
                if (this.arr.picUrl) {
                    if (document.getElementById('alter_pic').files[0]) {
                        formData.append("wxPic", document.getElementById('alter_pic').files[0]);
                    }

                } else {
                    alert("请上传微信投放图！");
                    return;
                }
            } else if (this.arr.adType == 2) {
                if (this.arr.textContent) {
                    formData.append("textContent", this.arr.textContent);
                } else {
                    alert("请填写文本内容！");
                    return;
                }
            } else {
                if (this.arr.picUrl) {
                    if (document.getElementById('alter_pic').files[0]) {
                        formData.append("wxPic", document.getElementById('alter_pic').files[0]);
                    }

                } else {
                    alert("请上传微信投放图！");
                    return;
                }
            }
            if (this.arr.coverPic) {
                if (document.getElementById('alter_portal').files[0]) {
                    formData.append("coverPic", document.getElementById('alter_portal').files[0]);
                }
            } else {
                alert("请上传广告展示图！");
                return;
            }
            if (this.arr.pushTime == '--') {
                alert("请填写投放时间！");
                return;
            } else {
                formData.append("pushTime", this.arr.pushTime);
            }
            if (Number(this.arr.clickAmount)) {
                formData.append("clickAmount", this.arr.clickAmount);
            } else {
                alert("请填写点击次数！");
                return;
            }
            $.ajax({
                url: apibase + "/admin/ad/" + id + "/update",
                data: formData,
                type: 'POST',
                cache: false,
                processData: false,
                contentType: false,
                success: function (resp) {
                    if (resp.retCode == RetCode.SUCCESS) {
                        alert("更新成功!");
                        _self.queryList(SaimiriPagination.defaults.curPage);
                        $("#ad_redact").modal("hide");
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
         * 数据 复位
         */
        int: function () {
            var date = new Date().getTime() + 86400000;
            this.arr = {
                adType: 1,
                pushType: 1,
                pushTime: $mks.formatDateTime(date),
                adRecommendStatement: ''
            };
            this.income = null;
            this.pushImg = null;
            this.portalImg = null;
            this.clickAmount = null;
            $("#portal_file").val('')
            $("#push_file").val('')
        },
        /**
         * 广告 投放
         */
        pushModal: function (id) {
            $("#ad_put").modal("show");
            var _self = this;
            this.adId = id;
            this.ofTagsList = [];
            _self.tagIds = [];
            $mks.syncJsonPost({
                url: apibase + "/admin/ad/" + id + "/distribute-wxpub",
                success_func: function (resp) {
                    if (resp.retCode == RetCode.SUCCESS) {
                        _self.putAllList = resp.result;
                        _self.tagsAllList = resp.result.tags;
                        _self.tagIds = resp.result.tagIdsOfAd;
                        _self.wxPubIds = resp.result.wxPubIdsOfAd;
                        _self.eachOfTags();
                        _self.allAccountsIdOfTags();
                    } else {
                        alert(resp.retMsg);
                        if (resp.retCode == RetCode.NO_LOGIN) {
                            location.href = base + '/admin/login.html';
                        }
                    }
                }
            });
            this.activeTagName = '';
        },
        selectTags: function (id, name) {
            var _self = this;
            var tagSign = this.tagIds.indexOf(id);
            if (tagSign == -1) {
                this.tagIds.push(id);
                this.addAccounts(id);
            } else {
                this.tagIds.splice(tagSign, 1);
                if (this.activeTagName == name) {
                    this.activeTagName = '';
                    this.wxPubs = []
                }
                this.delAccounts(id);
            }
            this.eachOfTags();
        },
        //  添加 标签  >>关联  公众号
        addAccounts: function (id) {
            var _self = this;
            $.each(_self.putAllList.tagId2WxPubsItems, function (i, n) {
                if (n.tagId == id) {
                    $.each(n.wxPubs, function (i, n) {
                        if (_self.allAccountsOfTags.indexOf(n.id) == -1) {
                            _self.allAccountsOfTags.push(n.id);
                            if (_self.wxPubIds.indexOf(n.id) == -1) {
                                _self.wxPubIds.push(n.id);
                            }
                        }
                    });
                    return false;
                }
            })
        },
        //  del 标签  >>关联  公众号
        delAccounts: function (id) {
            var _self = this;
            _self.allAccountsOfTags = [];
            $.each(this.tagIds, function (i, n) {
                var tagId = n;
                $.each(_self.putAllList.tagId2WxPubsItems, function (i, n) {
                    if (n.tagId == tagId) {
                        $.each(n.wxPubs, function (i, n) {
                            if (_self.allAccountsOfTags.indexOf(n.id) == -1) {
                                _self.allAccountsOfTags.push(n.id);
                            }
                        });
                        return false;
                    }
                })
            });
            $.each(_self.putAllList.tagId2WxPubsItems, function (i, n) {
                if (n.tagId == id) {
                    $.each(n.wxPubs, function (i, n) {
                        if (_self.allAccountsOfTags.indexOf(n.id) == -1) {
                            var key = _self.wxPubIds.indexOf(n.id);
                            if (key != -1) {
                                _self.wxPubIds.splice(key, 1);
                            }
                        }
                    });
                    return false;
                }
            })
        },
        activeTag: function (id, name) {
            var _self = this;
            this.queryKey = '';
            this.activeTagId = id;
            this.activeTagName = name;
            this.page = 1;
            //active 标签 所有关联的 ID 公众号
            $.each(_self.putAllList.tagId2WxPubsItems, function (i, n) {
                if (n.tagId == id) {
                    _self.wxPubs = n.wxPubs;
                    _self.wxPubsList = n.wxPubs;
                    return false;
                }
            });
            this.allStatus();
            this.updateList();
        },
        //选中 所有标签  关联的  公众号ID 合集
        allAccountsIdOfTags: function () {
            var _self = this;
            _self.allAccountsOfTags = [];
            $.each(this.tagIds, function (i, n) {
                var tagId = n;
                $.each(_self.putAllList.tagId2WxPubsItems, function (i, n) {
                    if (n.tagId == tagId) {
                        $.each(n.wxPubs, function (i, n) {
                            if (_self.allAccountsOfTags.indexOf(n.id) == -1) {
                                _self.allAccountsOfTags.push(n.id);
                            }
                        });
                        return false;
                    }
                })
            });
        },
        // 更新  推送 公众号 Id  合集
        updateWxPubIds: function (id) {
            var i = this.wxPubIds.indexOf(id);
            if (i == -1) {
                this.wxPubIds.push(id);
            } else {
                this.wxPubIds.splice(i, 1);
            }
            this.allStatus();
        },
        //全选  按钮  状态
        allStatus: function () {
            var _self = this;
            $.each(_self.wxPubsList, function (i, n) {
                if (_self.wxPubIds.indexOf(n.id) == -1) {
                    _self.allActive = false;
                    return false;
                }
                _self.allActive = true;
            });
            this.pages = Math.ceil(_self.wxPubsList.length / 13);
            Vue.component("web-page", {
                template: _self.webPaging()
            });
        },
        //全选  按钮
        allTarget: function () {
            var _self = this;
            if (this.allActive == true) {
                this.allActive = false;
                $.each(_self.wxPubsList, function (i, n) {
                    var key = _self.wxPubIds.indexOf(n.id);
                    if (key != -1) {
                        _self.wxPubIds.splice(key, 1);
                    }
                });
            } else {
                this.allActive = true;
                $.each(_self.wxPubsList, function (i, n) {
                    var key = _self.wxPubIds.indexOf(n.id);
                    if (key == -1) {
                        _self.wxPubIds.push(n.id);
                    }
                });
            }
        },
        // 搜索  公众号
        queryAccounts: function () {
            var _self = this;
            if ($.trim(this.queryKey) != '') {
                var list = [];
                $.each(this.wxPubs, function (i, n) {
                    if ((n.nickname).indexOf($.trim(_self.queryKey)) != -1) {
                        list.push(n);
                    }
                });
                this.wxPubsList = list;
            }
            this.updateList();
        },
        //更新 公众号 list
        updateList: function () {
            var _self = this;
            $('#ad_put .web-page').html(_self.webPaging());
        },
        putSubmit: function () {
            var _self = this;
            if (_self.tagIds.length == 0) {
                alert("至少选择一个标签！");
                return;
            }
            if (_self.wxPubIds.length == 0) {
                alert("至少选择一个公众号！");
                return;
            }
            $mks.syncJsonPost({
                url: apibase + "/admin/ad/distribute-wxpub-confirm",
                data: {
                    adId: _self.adId,
                    tagIds: _self.tagIds,
                    wxPubIds: this.wxPubIds
                },
                success_func: function (resp) {
                    if (resp.retCode == RetCode.SUCCESS) {
                        alert('修改成功！');
                        $("#ad_put").modal("hide");
                    } else {
                        alert(resp.retMsg);
                        if (resp.retCode == RetCode.NO_LOGIN) {
                            location.href = base + '/admin/login.html';
                        }
                    }
                }
            });
        },
        /**
         * 遍历  关联 标签
         */
        eachOfTags: function () {
            var _self = this;
            var list = [];
            $.each(_self.tagIds, function (i, n) {
                var tagId = n;
                $.each(_self.tagsAllList, function (i, n) {
                    if (n.id == tagId) {
                        list.unshift(n);
                        return false;
                    }
                })
            });
            this.ofTagsList = list;
        },
        webPaging: function () {
            var _self = this;
            var html = "";
            if (_self.pages - _self.page < 6) {
                for (var i = _self.pages - 6; i <= _self.pages; i++) {
                    if (i > 0) {
                        if (i == _self.page) {
                            html += "<button class='active'>" + i + "</button>"
                        } else {
                            html += "<button>" + i + "</button>"
                        }
                    }
                }
            } else {
                for (var i = 0; i <= 2; i++) {
                    if (_self.page == 1) {
                        var p = Number(_self.page) + Number(i);
                        if (p == _self.page) {
                            html += "<button class='active'>" + p + "</button>"
                        } else {
                            html += "<button>" + p + "</button>"
                        }
                    } else {
                        var p = Number(_self.page) + Number(i) - 1;
                        if (p == _self.page) {
                            html += "<button class='active'>" + p + "</button>"
                        } else {
                            html += "<button>" + p + "</button>"
                        }
                    }
                }
                html += "<button>" + "···" + "</button>" + "<button>" + ((_self.pages) - 1) + "</button>" + "<button>" + _self.pages + "</button>"
            }
            return html;
        }
    },
    filters: {
        formatTimeStamp: function (time) {
            return $mks.formatUnixTimeStamp(time);
        },
        // 广告类型
        formatAdType: function (type) {
            var arr = ['', '图文类型', '文本类型', '图片类型', '问问搜类型'];
            return arr[type]
        },
        //推送类型
        formatPushType: function (type) {
            var arr = ['', '聊天时推送', '48小时推送'];
            return arr[type]
        },
        //投放状态
        pushStateType: function (type) {
            var arr = ['未投放', '预投放', '投放中', '已停止']
            return arr[type]
        },
        //关闭状态
        closeStateType: function (type) {
            var arr = ['是', '否']
            return arr[type]
        }
    }
})
