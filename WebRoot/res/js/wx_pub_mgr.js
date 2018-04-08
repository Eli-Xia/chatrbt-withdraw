// Child from wx_pub_news_mgr.js

var wxPubMgrVue = new Vue({
    el: '#wx_pub_mgr',
    data: {
        wxPubList: [],
        page: 1,
        curName: '',
        originId: '',
        tagList: [],
        currentTags: [],
        currentPubId: null,
        showDetail: false,
    },
    components: {
        'wx-news-mgr': Child
    },
    created: function () {
        var _self = this;
        $.extend(SaimiriPagination.defaults, {curPage: 1, total: 0, pageSize: 10});
        _self.queryList(1);
        _self.getAllTags();
        $("#wx_pub_mgr").on("click", "#page button", function (e) {
            var text = e.target.innerHTML;
            if (Number(text)) {
                if (SaimiriPagination.defaults.curPage != Number(text)) {
                    _self.queryList(text);
                }
            }
        });
    },
    methods: {
        /**
         * 获取用户列表
         */
        queryList: function (page) {
            var _self = this;
            $mks.syncJsonPost({
                data: {
                    page: page,
                    pageSize: SaimiriPagination.defaults.pageSize
                },
                url: apibase + '/admin/wx/pub/list',
                success_func: function (resp) {
                    if (resp.retCode == RetCode.SUCCESS) {
                        _self.wxPubList = resp.result;
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
        /**
         * 点击设置管理员按钮
         */
        onBtnSetAdm: function (name, wxPubId) {
            this.curName = name;
            this.originId = wxPubId;
            this.showDetail = true;
        },
        // 获取公众号标签
        getPubTag: function (id, callback) {
            $mks.syncJsonPost({
                data: {},
                url: apibase + '/admin/wxPub-tag/' + id + '/list',
                success_func: callback
            })
        },
        editModalShow: function (id, tags) {
            var _self = this;
            _self.currentTags = [];
            tags.map(function (tag) {
                _self.currentTags.push(tag.id);
            })
            _self.currentPubId = id;
            $('#modal_edit_tags').modal('show');
            _self.tagList.map(function (item) {
                item.active = false;
                tags.forEach(function (e) {
                    if (item.id == e.id) {
                        item.active = true;
                    }
                })
            })
        },
        // 获取所有标签
        getAllTags: function () {
            var _self = this;
            $mks.syncJsonPost({
                data: {},
                url: apibase + '/admin/wxPub-tag/list',
                success_func: function (res) {
                    if (res.retCode == RetCode.SUCCESS) {
                        res.result.forEach(function (e) {
                            e.active = false;
                        })
                        _self.tagList = res.result;
                    } else {
                        alert(res.retMsg);
                        if (res.retCode == RetCode.NO_LOGIN) {
                            location.href = base + '/admin/login.html';
                        }
                    }
                }
            })
        },
        // 选择标签
        selectTag: function (id) {
            var _self = this;
            var idx = this.currentTags.indexOf(id);
            if (idx > -1) {
                this.currentTags.splice(idx, 1);
            } else {
                this.currentTags.push(id);
            }
            var clength = this.currentTags.length;
            this.currentTags = this.currentTags.slice(clength > 3 ? clength - 3 : 0, clength);
            _self.tagList.map(function (item) {
                item.active = false;
                _self.currentTags.forEach(function (e) {
                    if (item.id == e) {
                        item.active = true;
                    }
                })
            })
        },
        // 设置标签
        setTags: function () {
            var _self = this;
            $mks.syncJsonPost({
                data: {},
                url: apibase + '/admin/wxPub-tag/wx-pub/' + _self.currentPubId + '/update?ids=' + _self.currentTags,
                success_func: function (res) {
                    if (res.retCode == RetCode.SUCCESS) {
                        $('#modal_edit_tags').modal('hide');
                        _self.queryList(SaimiriPagination.defaults.curPage);
                    } else {
                        alert(res.retMsg);
                        if (res.retCode == RetCode.NO_LOGIN) {
                            location.href = base + '/admin/login.html';
                        }
                    }
                }
            })
        },
    },
    filters: {
        formateTimeStamp: function (time) {
            return $mks.formatUnixTimeStamp(time);
        },
        formatStatus: function (type) {
            return type == 1 ? "已接入" : '未接入'
        }
    }
})