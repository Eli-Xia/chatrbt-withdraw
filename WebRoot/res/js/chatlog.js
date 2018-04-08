var wxPubMgrVue = new Vue({
    el: '#chatlog',
    data: {
        list: [],
        page: 1,
        curOpItemId: null,
        accounts: "",
        show: false
    },
    created: function () {
        var _self = this;
        $.extend(SaimiriPagination.defaults, {curPage: 1, total: 0, pageSize: 200});
        _self.queryList(1);
        $("#chatlog").on("click", ".page button", function (e) {
            var text = e.target.innerHTML;
            if (Number(text)) {
                _self.queryList(Number(text));
            } else if (text == "首页") {
                if (SaimiriPagination.defaults.curPage != 1) {
                    _self.queryList(1);
                }
            } else if (text == "尾页") {
                if (SaimiriPagination.defaults.curPage != SaimiriPagination.defaults.pageCount) {
                    _self.queryList(SaimiriPagination.defaults.pageCount);
                }
            }
        });
        var date = new Date().getTime();
        date = $mks.formatDateTime(date);
        $(function () {
            $('#lay_date').daterangepicker({
                "maxDate": date,
                'parentEl': ".box-header .date-box",
                "windowTarget":true,
                "locale": {
                    format: 'YYYY-MM-DD',
                    separator: ' ~ ',
                }
            }, function (start, end, label) {
                beginTimeStore = start;
                endTimeStore = end;
                console.log(this.startDate.format(this.locale.format));
                console.log(this.endDate.format(this.locale.format));
                if (!this.startDate) {
                    this.element.val('');
                } else {
                    this.element.val(this.startDate.format(this.locale.format) + this.locale.separator + this.endDate.format(this.locale.format));
                }
            });
            $('#lay_date').val('');
        })
    },
    methods: {
        /**
         * 获取列表
         */
        queryList: function (page) {
            var _self = this;
            var page = page;
            $mks.syncJsonPost({
                data: {
                    page: page,
                    pageSize: SaimiriPagination.defaults.pageSize
                },
                url: apibase + '/admin/chatlog/list',
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
         * 条件查询
         */
        queryCondition: function (page) {
            var _self = this;
            if ($("#lay_date").val().trim() != "" || _self.accounts.trim() != '') {
                alert("条件查询!");
            } else {
                _self.queryList(1);
            }

        },
        clearCondition: function () {
            this.accounts = "";
            $("#lay_date").val('');
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
        notNullToR: function (str) {
            if (str == null) {
                return 'A: ';
            } else {
                return 'R: ';
            }
        }
    }
})