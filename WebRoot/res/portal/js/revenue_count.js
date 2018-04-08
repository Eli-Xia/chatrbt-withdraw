new Vue({
    el: '#revenue',
    data: {
        endDate: '',
        maxDate: '',
        startDate: '',
        page: 1,
        pages: 1,
        activeTime: '2018-01-16',
        income: '--',
        showDetail: false,
        yesterdayIncome: '--',
        incomeList: [],
        accountsList: [],
        accountPage: 1,
        accountPages: 1
    },
    created: function () {
        var _self = this;
        var date = new Date().getTime() - 86400000;
        this.endDate = $mks.formatDateTime(date);
        this.maxDate = $mks.formatDateTime(date);
        this.startDate = $mks.formatDateTime(date - (86400000 * 6));
        $('#income_time').daterangepicker({
            "startDate": _self.startDate,
            "endDate": _self.endDate,
            "maxDate": _self.maxDate,
            'autoApply': true,
            'parentEl': ".revenue-detail>.time-box",
            'dateLimit': {
                days: 180  //最大  选取范围
            },
            "locale": {
                format: 'YYYY-MM-DD',
                separator: ' ~ ',
            }
        }, function (start, end, label) {
            var start = new Date(start._d).getTime();
            var end = new Date(end._d).getTime();
            _self.startDate = $mks.formatDateTime(start);
            _self.endDate = $mks.formatDateTime(end);
            _self.queryIncomeList(1)
        });
        this.queryIncomeList(1);
        $('#revenue').on('click', '.revenue-list .income-page button', function (e) {
            var text = e.target.innerHTML;
            var key = 1;
            if (text == '首页') {
                if (_self.page == 1) {
                    return;
                }
                key = 1;
            } else if (text == '尾页') {
                if (_self.page == _self.pages) {
                    return;
                }
                key = _self.pages;
            } else if (Number(text)) {
                if (_self.page == Number(text)) {
                    return;
                }
                key = Number(text);
            } else if (text == '«') {
                if (_self.page > 1) {
                    key = _self.page - 1
                } else {
                    return
                }
            } else if (text == '»') {
                if (_self.page < _self.pages) {
                    key = _self.page + 1
                } else {
                    return
                }
            }
            _self.queryIncomeList(key)
        }).on('click', '.total-footer .income-page button', function (e) {
            var text = e.target.innerHTML;
            if (text == '首页') {
                _self.accountPage = 1;
            } else if (text == '尾页') {
                _self.accountPage = _self.accountPages;
            } else if (Number(text)) {
                _self.accountPage = Number(text);
            } else if (text == '«') {
                if (_self.accountPage > 1) {
                    _self.accountPage = _self.accountPage - 1
                }
            } else if (text == '»') {
                if (_self.accountPage < _self.accountPages) {
                    _self.accountPage = _self.accountPage + 1
                }
            }
            _self.accountsPaging();
        })
    },
    methods: {
        queryIncomeList: function (key) {
            var _self = this;
            $("#list_layer").show();
            $mks.syncJsonPost({
                data: {
                    startDate: _self.startDate,
                    endDate: _self.endDate,
                    page: key
                },
                url: apibase + '/income/user/daily/list',
                success_func: function (resp) {
                    if (resp.retCode == RetCode.SUCCESS) {
                        _self.incomeList = resp.result.userDaliyInocmeOverviewList;
                        _self.yesterdayIncome = resp.result.yesterdayIncome;
                        _self.page = key;
                        _self.pages = Math.ceil(resp.total / 8);
                        $(".revenue-list .web-page").html(SaimiriPagination.webPaging(_self.page, _self.pages));
                    } else {
                        dialog(resp.retMsg, 2);
                    }
                    $("#list_layer").hide();
                    $("#layer").hide();
                }
            })
        },
        detailModel: function (date, income) {
            if (this.showDetail) {
                $("#global_layer").hide();
                this.showDetail = false
            } else {
                this.activeTime = date;
                this.income = income;
                $("#global_layer").show();
                this.showDetail = true;
                this.queryAccounts(1)
            }
        },
        queryAccounts: function (key) {
            var _self = this;
            $mks.syncJsonPost({
                data: {
                    date: $mks.formatDateTime(_self.activeTime),
                    page: key
                },
                url: apibase + '/income/user/daily/detail',
                success_func: function (resp) {
                    if (resp.retCode == RetCode.SUCCESS) {
                        _self.accountsList = resp.result.wxPubDailyIncomeOverviewList;
                        _self.accountPage = key;
                        _self.accountPages = Math.ceil(resp.total / 4);
                        $(".detail-layer .web-page").html(SaimiriPagination.webPaging(_self.accountPage, _self.accountPages))
                    } else {
                        dialog(resp.retMsg, 2);
                    }
                }
            })
        },
        accountsPaging: function () {
            $(".detail-layer .web-page").html(SaimiriPagination.webPaging(this.accountPage, this.accountPages))
        }
    },
    filters: {
        formatDateTime: function (time) {
            return $mks.formatDateTime(time);
        }
    }
})