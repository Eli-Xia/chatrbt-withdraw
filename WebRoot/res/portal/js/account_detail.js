var Detail = new Vue({
    el: '#account_detail',
    data: {
        robotMessage: '',
        characterList: [],
        intRobot: false,
        active: "active",//class
        reply: '', //是否自动回复
        keyword: "", //关键词
        replyContent: "",// 回复内容
        writeBack: 0,//回复方式
        list: [],
        showId: 1,// section  显示ID
        topList: {},
        imgUrl: base + "/res/portal/images/default.png",
        settingSelect: true,
        addShow: false,  //添加关键词 model
        replyList: {},
        condition: "",//搜索关键词
        delId: 0,
        detailIndex: 0,
        mouseY: "",
        detailShow: false,
        alterShow: false,
        show: false,
        startDate: "",
        endDate: "",
        maxDate: "",
        menActive: 0,
        chatActive: 0,
        params: {},
        menDate: [],
        menValue: [],
        menLength: 1,
        chatDate: [],
        chatValue: [],
        chatLength: 1,
        yesterdayMenNum: "--",
        addMenNum: "--",
        yesterdayChatNum: "--",
        addChatNum: "--",
        historySign: {//进入标记  为true 不请求
            men: false,
            num: false,
            robotSet: false,
            keywordSet: false,
            income: false
        },
        keywordPage: {},
        faceShow: false,
        yesterdayIncome: '--',//昨日收益
        totalIncome: '--',//历史总收益
        incomeList: [],
        page: 1,
        pages: 1
    },
    watch: {
        reply: function (newval, oldval) {
            if (newval == true && this.replyList.length == undefined) {
                this.queryList(1, 3);
            }
        },
        robotMessage: {
            handler: function (newValue, oldValue) {
                if (oldValue != '') {
                    this.intRobot = true;
                }
            },
            deep: true
        },
        condition: function (newval, oldval) {
            if (newval == "") {
                this.queryList(1, 3);
            }
        }
    },
    computed: {
        replyContentLength: function () {
            return 300 - this.replyContent.length
        }
    },
    created: function () {
        var _self = this;
        $("#detail_layer").show();
        $.extend(SaimiriPagination.defaults, {curPage: 1, total: 0, pageSize: 30});
        _self.queryList(1, 1);
        $("#account_detail").on("click", ".chat .page button", function (e) {
            var text = e.target.innerHTML;
            if (Number(text)) {
                _self.queryList(Number(text), 1);
            } else if (text == "首页") {
                if (SaimiriPagination.defaults.curPage != 1) {
                    _self.queryList(1, 1);
                }
            } else if (text == "尾页") {
                if (SaimiriPagination.defaults.curPage != SaimiriPagination.defaults.pageCount) {
                    _self.queryList(SaimiriPagination.defaults.pageCount, 1);
                }
            }
        }).on("click", ".settings .page button", function (e) {
            var text = e.target.innerHTML;
            if (Number(text)) {
                if (SaimiriPagination.defaults.curPage != text) {
                    _self.queryList(Number(text), 3);
                }
            } else if (text == "首页") {
                if (SaimiriPagination.defaults.curPage != 1) {
                    _self.queryList(1, 3);
                }
            } else if (text == "尾页") {
                if (SaimiriPagination.defaults.curPage != SaimiriPagination.defaults.pageCount) {
                    _self.queryList(SaimiriPagination.defaults.pageCount, 3);
                }
            }
        });
        //  获取  机器人 基本数据
        $mks.syncJsonPost({
            url: apibase + "/wx-pub/count-base-info/" + wxPubOriginId,
            async: true,
            success_func: function (resp) {
                if (resp.retCode == RetCode.SUCCESS) {
                    _self.topList = resp.result;
                }
            }
        });
        $(document).bind('mouseup', function (e) {
            e.stopPropagation();
            _self.mouseY = e.pageY;

            if (e.target.nodeName == 'HTML') {
                return;
            }
            if (e.target.parentElement.className == 'qq-face' || e.target.className == 'face-select' || e.target.className == 'qq-face') {
                return;
            }
            _self.faceShow = false;
        });
        $("#account_detail").on('click', '.qq-face img', function (e) {
            console.log($(e.target).attr('data-index'));
        })
    },
    methods: {
        //  section模块显示
        showModel: function (key) {
            var _self = this;
            if (key == 1) {
                $("#detail_layer").show();
                this.showId = key;
                $.extend(SaimiriPagination.defaults, {curPage: 1, total: 0, pageSize: 30});
                this.queryList(1, 1);
            } else if (key == 3) {
                if (this.showId != key) {
                    this.showId = key;
                    _self.params = {
                        url: '/chatMan-chart',
                        el: "chart_man",
                        color: "#28a7fd",
                        parentEl: "#people_time",
                        name: "聊天总人数"
                    };
                    createEchart(_self.params);
                    updateEchart(this.menDate, this.menValue, this.menLength);
                    if (this.historySign.men) {
                        return;
                    }
                    _self.menActive = 0;
                    _self.intTime(1);
                }
            } else if (key == 4) {
                if (this.showId != key) {
                    this.showId = key;
                    _self.params = {
                        url: '/chatNum-chart',
                        el: "chart_num",
                        color: "#ffd553",
                        parentEl: "#chat_time",
                        name: "聊天总次数"
                    };
                    createEchart(_self.params);
                    updateEchart(this.chatDate, this.chatValue, this.chatLength);
                    if (this.historySign.chat) {
                        return;
                    }
                    _self.chatActive = 0;
                    _self.intTime(1);
                }
            } else if (key == 5) {
                if (this.showId != key) {
                    this.showId = key;
                    $.extend(SaimiriPagination.defaults, {curPage: 1, total: 0, pageSize: 10});
                    $.extend(SaimiriPagination.defaults, this.keywordPage);
                    if (this.historySign.robotSet) {
                        return;
                    }
                    this.queryList(1, 2);
                }
            } else if (key == 2) {
                if (this.showId != key) {
                    var _self = this;
                    this.showId = key;
                    if (this.historySign.income == true) {
                        return;
                    }
                    $("#detail_layer").show();
                    //获取基本收益
                    $mks.syncJsonPost({
                        data: {
                            wxPubOriginId: wxPubOriginId
                        },
                        url: apibase + '/income/wx-pub/base/count',
                        success_func: function (resp) {
                            if (resp.retCode == RetCode.SUCCESS) {
                                $("#detail_layer").hide();
                                _self.yesterdayIncome = resp.result.yesterdayIncome;
                                _self.totalIncome = resp.result.historyTotalIncome;
                            } else {
                                dialog(resp.retMsg, 2);
                            }
                        }
                    })
                    var date = new Date().getTime() - 86400000;
                    this.endDate = $mks.formatDateTime(date);
                    this.maxDate = $mks.formatDateTime(date);
                    this.startDate = $mks.formatDateTime(date - (86400000 * 6));
                    $('#income_time').daterangepicker({
                        "startDate": _self.startDate,
                        "endDate": _self.endDate,
                        "maxDate": _self.maxDate,
                        'autoApply': true,
                        'parentEl': ".revenue-stats>div",
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
                        _self.queryIncomeList(1);
                    });
                    this.queryIncomeList(1);
                    $('.earnings').on('click', '.income-page button', function (e) {
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
                        _self.queryIncomeList(key);
                    })
                }
            }
        },
        /**
         *   请求 聊天人数  和  聊天次数  详情
         * @param params
         */
        queryChart: function () {
            var _self = this;
            $("#chart_layer").show();
            $mks.syncJsonPost({
                url: apibase + '/chat-log/' + wxPubOriginId + _self.params.url,
                data: {
                    beginDate: this.startDate,
                    endDate: this.endDate
                },
                success_func: function (resp) {
                    if (resp.retCode == RetCode.SUCCESS) {
                        var xDate = [], yValue = [], yLength = 0;
                        $.each(resp.result.nodes, function (i, n) {
                            xDate.push($mks.formatDateTime(n.date));
                            yValue.push(n.amount);
                            if (n.amount.toString().length / 3 > yLength) {
                                yLength = Math.ceil(n.amount.toString().length / 3)
                            }
                        });
                        if (resp.result.chatManNum != undefined) {
                            _self.yesterdayMenNum = resp.result.chatManNum;
                            _self.addMenNum = resp.result.boostManNum;
                            _self.historySign.men = true;
                            _self.menDate = xDate;
                            _self.menValue = yValue;
                            _self.menLength = yLength;
                        } else {
                            _self.yesterdayChatNum = resp.result.chatNum;
                            _self.addChatNum = resp.result.boostNum;
                            _self.historySign.chat = true;
                            _self.chatDate = xDate;
                            _self.chatValue = yValue;
                            _self.chatLength = yLength;
                        }
                        updateEchart(xDate, yValue, yLength);
                    } else {
                        dialog(resp.retMsg, 2);
                    }
                    $("#chart_layer").hide();
                }
            });
        },
        /**
         * 初始化 时间
         */
        intTime: function (key) {
            var _self = this;
            var date = new Date().getTime() - 86400000;
            if (this.showId == 3) {
                if (this.menActive == key) {
                    return;
                } else {
                    this.menActive = key
                }
            }
            if (this.showId == 4) {
                if (this.chatActive == key) {
                    return;
                } else {
                    this.chatActive = key
                }
            }
            if (key == 1) {
                this.endDate = $mks.formatDateTime(date);
                this.maxDate = $mks.formatDateTime(date);
                this.startDate = $mks.formatDateTime(date - (86400000 * 6));
            } else {
                this.endDate = $mks.formatDateTime(date);
                this.startDate = $mks.formatDateTime(date - (86400000 * 29));
            }
            this.queryChart();
            $(this.params.parentEl).daterangepicker({
                "startDate": _self.startDate,
                "endDate": _self.endDate,
                "maxDate": _self.maxDate,
                'autoApply': true,
                'parentEl': ".chart-query>div",
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
                if (_self.showId == 3) {
                    _self.menActive = 3
                } else {
                    _self.chatActive = 3
                }
                _self.startDate = $mks.formatDateTime(start);
                _self.endDate = $mks.formatDateTime(end);
                _self.queryChart();
            });
        },
        //请求
        queryList: function (page, key) {
            var _self = this;
            var page = page;
            if (key == 1) {
                //聊天记录
                $mks.syncJsonPost({
                    data: {
                        page: page,
                        pageSize: SaimiriPagination.defaults.pageSize,
                        wxPubOriginId: wxPubOriginId
                    },
                    url: apibase + '/chat-log/list',
                    success_func: function (resp) {
                        if (resp.retCode == RetCode.SUCCESS) {
                            _self.list = resp.result;
                            $("#detail_layer").hide();
                            $.extend(SaimiriPagination.defaults, {
                                curPage: page,
                                total: resp.total,
                                pageCount: Math.ceil(resp.total / SaimiriPagination.defaults.pageSize)
                            });
                            $("#page").html(SaimiriPagination.paging());
                        } else {
                            dialog(resp.retMsg, 2);
                        }
                    }
                })
            } else if (key == 2) {
                this.robotMessage = '';
                //获取机器人config信息
                $mks.syncJsonPost({
                    url: apibase + '/chat-robot/' + wxPubOriginId,
                    success_func: function (resp) {
                        if (resp.retCode == RetCode.SUCCESS) {
                            $.each(resp.result.characterList, function (i, n) {
                                resp.result.characterList[i] = n.toString();
                            });
                            resp.result.gender.toString();
                            _self.robotMessage = resp.result;
                            _self.characterList = resp.result.characterList;
                            _self.historySign.robotSet = true;
                        } else {
                            dialog(resp.retMsg, 2);
                        }
                        _self.intRobot = false;
                    }
                })
            } else if (key == 3) {
                // 关键词回复请求
                $mks.syncJsonPost({
                    data: {
                        page: page,
                        pageSize: SaimiriPagination.defaults.pageSize,
                        wxPubOriginId: wxPubOriginId,
                        keywords: this.condition
                    },
                    url: apibase + '/kr/list',
                    success_func: function (resp) {
                        if (resp.retCode == RetCode.SUCCESS) {
                            _self.replyList = resp.result;
                            _self.keywordPage = {
                                curPage: page,
                                total: resp.total,
                                pageCount: Math.ceil(resp.total / SaimiriPagination.defaults.pageSize)
                            }
                            $.extend(SaimiriPagination.defaults, _self.keywordPage);
                        } else {
                            dialog(resp.retMsg, 2);
                        }
                        Vue.component("btn-list", {
                            template: SaimiriPagination.paging()
                        });
                        _self.show = true;
                    }
                })
            }
        },
        /**
         * 向前翻页
         */
        lastPage: function (key) {
            if (SaimiriPagination.defaults.curPage - 1 > 0) {
                SaimiriPagination.defaults.curPage -= 1;
                if (key == 1) {
                    this.queryList(SaimiriPagination.defaults.curPage, 1);
                } else if (key == 3) {
                    this.queryList(SaimiriPagination.defaults.curPage, 3);
                }
            }
        },
        /**
         * 向后翻页
         */
        nextPage: function (key) {
            if (SaimiriPagination.defaults.curPage + 1 <= SaimiriPagination.defaults.pageCount) {
                SaimiriPagination.defaults.curPage += 1;
                if (key == 1) {
                    this.queryList(SaimiriPagination.defaults.curPage, 1);
                } else if (key == 3) {
                    this.queryList(SaimiriPagination.defaults.curPage, 3);
                }
            }
        },
        // 设置  选项
        settingToggle: function (key) {
            if (key == false) {
                if (this.settingSelect != key) {
                    this.settingSelect = key;
                    if (this.historySign.keywordSet) {
                        return;
                    }
                    this.queryReplyStatus();
                }
            } else {
                if (this.settingSelect != key) {
                    this.settingSelect = key;
                    if (this.historySign.robotSet) {
                        return;
                    }
                    this.queryList(1, 2);
                }
            }
        },
        /**
         *   取消修改机器人config信息
         */
        resetMessage: function () {
            if (this.intRobot == true) {
                this.queryList(1, 2);
            }
            this.intRobot = false;
        },
        /**
         *   save机器人config信息
         */
        saveMessage: function () {
            var _self = this;
            if (this.intRobot == true) {
                if ($.trim(_self.robotMessage.nickname) == '') {
                    dialog("带 * 号必选!", 2);
                    return;
                }
                if (_self.robotMessage.characterList.length == 0) {
                    dialog("带 * 号必选!", 2);
                    return;
                }
                _self.robotMessage.gender = Number(_self.robotMessage.gender);
                $mks.syncJsonPost({
                    data: _self.robotMessage,
                    url: apibase + '/chat-robot/update',
                    success_func: function (resp) {
                        if (resp.retCode == RetCode.SUCCESS) {
                            dialog("修改成功!", 1);
                        } else {
                            dialog(resp.retMsg, 2);
                        }
                    }
                })
            }
            this.intRobot = false;
        },
        /**
         * 获取 用户 自动回复  状态
         */
        queryReplyStatus: function () {
            var _self = this;
            $mks.syncJsonPost({
                url: apibase + '/kr/status/' + wxPubOriginId,
                success_func: function (resp) {
                    if (resp.retCode == RetCode.SUCCESS) {
                        _self.reply = resp.result == 0 ? false : true;
                        _self.historySign.keywordSet = true;
                    } else {
                        dialog(resp.retMsg, 2);
                    }
                }
            })
        },
        /**
         *   自动 回复 开关
         */
        updateReplyStatus: function () {
            var _self = this;
            var status = _self.reply == true ? 0 : 1;
            $mks.syncJsonPost({
                url: apibase + '/kr/update/' + wxPubOriginId + '/status',
                data: {
                    status: status
                },
                success_func: function (resp) {
                    if (resp.retCode == RetCode.SUCCESS) {
                        // dialog("修改成功!", 1);
                        _self.reply = !_self.reply;
                    } else {
                        dialog(resp.retMsg, 2);
                    }
                }
            })
        },
        /**
         * 修改  回复
         */
        alterModel: function (i) {
            this.detailIndex = i;
            this.int();
            this.alterShow = true;
            this.keyword = this.replyList[i].keywords.join();
            this.replyContent = this.replyList[i].response;
        },
        hideAlter: function () {
            this.alterShow = false;
            this.faceShow = false
        },
        updateReply: function () {
            var data = {};
            var _self = this;
            if (this.keyword == this.replyList[this.detailIndex].keywords.join() && this.replyContent == this.replyList[this.detailIndex].response) {
                _self.alterShow = false;
                return;
            }
            if ($.trim(this.keyword) == '') {
                dialog("请填写关键词!", 2);
                return;
            } else {
                data.keywords = this.keyword.split(",");
            }
            if ($.trim(this.replyContent) == '') {
                dialog("请填写回复内容!", 2);
                return;
            } else {
                data.response = this.replyContent
            }
            data.rule = 1;
            data.id = this.replyList[this.detailIndex].keywordsId;
            $mks.syncJsonPost({
                data: data,
                url: apibase + '/kr/update',
                success_func: function (resp) {
                    if (resp.retCode == RetCode.SUCCESS) {
                        dialog("修改成功!", 1);
                        _self.queryList(SaimiriPagination.defaults.curPage, 3);
                        _self.alterShow = false;
                    } else {
                        dialog(resp.retMsg, 2);
                    }
                }
            })
        },
        //添加  关键词回复
        addModel: function () {
            this.addShow = !this.addShow;
            //int
            this.detailShow = false;
            this.alterShow = false;
            this.faceShow = false;
            $("#delete").hide();
            this.keyword = "";
            this.replyContent = ""
        },
        saveReply: function () {
            var data = {};
            var _self = this;
            if ($.trim(this.keyword) == '') {
                dialog("请填写关键词!", 2);
                return;
            } else {
                data.keywords = this.keyword.split(",");
            }
            if ($.trim(this.replyContent) == '') {
                dialog("请填写回复内容!", 2);
                return;
            } else {
                data.response = this.replyContent
            }
            data.rule = 1;
            data.wxPubOriginId = wxPubOriginId;
            $mks.syncJsonPost({
                data: data,
                url: apibase + '/kr/set',
                success_func: function (resp) {
                    if (resp.retCode == RetCode.SUCCESS) {
                        dialog("添加成功!", 1);
                        _self.condition = '';
                        _self.queryList(1, 3);
                        _self.addShow = false;
                    } else {
                        dialog(resp.retMsg, 2);
                    }
                }
            })
        },
        //删除  关键词回复
        delModel: function (i) {
            this.delId = i;
            this.int();
            // var top = event.pageY-450;
            var top = this.mouseY - 450;
            $("#delete").show().css({top: top})
        },
        hideDel: function () {
            $("#delete").hide()
        },
        delReply: function () {
            var _self = this;
            $mks.syncJsonPost({
                url: apibase + '/kr/delete/' + this.delId,
                success_func: function (resp) {
                    if (resp.retCode == RetCode.SUCCESS) {
                        dialog("删除成功!", 1);
                        _self.queryList(SaimiriPagination.defaults.curPage, 3);
                    } else {
                        dialog(resp.retMsg, 2);
                    }
                    $("#delete").hide();
                }
            })
        },
        /**
         *  查看  详情
         */
        detailModel: function (i) {
            this.detailIndex = i;
            this.int();
            this.detailShow = true;
        },
        hideDetail: function () {
            this.detailShow = false;
        },
        int: function () {
            this.addShow = false;
            this.alterShow = false;
            this.detailShow = false;
            $("#delete").hide();
        },
        faceModel: function () {
            this.faceShow = !this.faceShow;
        },
        //收益统计列表
        queryIncomeList: function (key) {
            var _self = this;
            $("#list_layer").show();
            $mks.syncJsonPost({
                data: {
                    wxPubOriginId: wxPubOriginId,
                    startDate: _self.startDate,
                    endDate: _self.endDate,
                    page: key
                },
                url: apibase + '/income/wx-pub/daily/count',
                success_func: function (resp) {
                    if (resp.retCode == RetCode.SUCCESS) {
                        _self.incomeList = resp.result;
                        _self.historySign.income = true;
                        _self.page = key;
                        _self.pages = Math.ceil(resp.total / 8);
                        $(".web-page").html(SaimiriPagination.webPaging(_self.page, _self.pages));
                    } else {
                        dialog(resp.retMsg, 2);
                    }
                    $("#list_layer").hide();
                }
            })
        }
    },
    filters: {
        formatTimeStamp: function (time) {
            return $mks.formatUnixTimeStamp(time);
        },
        formatDateTime: function (time) {
            return $mks.formatDateTime(time);
        },
        roleType: function (str) {
            if (str == 1) {
                return 'A';
            } else {
                return 'R';
            }
        }
        ,
    }
});
Vue.component("face-list", {
    template: faceTemplate()
});

function faceTemplate() {
    var html = '';
    for (var i = 1; i <= 75; i++) {
        var em = "[em_" + i + "]";
        html += "<img src=" + base + "/res/portal/face-list/" + i + ".gif data-index=" + em + ">"
    }
    return html;
}

var myChart;

function createEchart(params) {
    myChart = echarts.init(document.getElementById(params.el));
    option = {
        xAxis: {
            type: 'category',
            axisTick: {
                show: false,
            },
            axisLabel: {interval: 'auto'},
            data: ''
        },
        tooltip: {
            trigger: 'axis',
            backgroundColor: "rgba(40,167,253,0.7)",
            axisPointer: {
                type: 'line',
                lineStyle: {
                    color: '#eff6fb',
                    width: 2,
                    type: 'solid'
                },
            },
            textStyle: {
                color: "#000"
            },
            formatter: '{b}<br/>{a} : {c}',
        },
        yAxis: {
            type: 'value',
            minInterval: 1,
            boundaryGap: [0, 0.1],
            axisTick: {
                show: false
            },
            axisLine: {
                show: false,
            }
        },
        grid: {
            x: 30,
            y: 30,
            x2: 15,
            y2: 30,
        },
        series: [{
            data: '',
            type: 'line',
            name: params.name,
            symbol: "circle",
            symbolSize: 6,
            showAllSymbol: true,
            itemStyle: {
                normal: {
                    color: params.color,
                    borderWidth: 2,
                    borderColor: "#fff",
                    lineStyle: {
                        color: params.color
                    }
                }
            }
        }]
    };
    myChart.setOption(option);
}

function updateEchart(x, y, l) {
    myChart.setOption({
        grid: {
            x: l * 30
        },
        xAxis: {
            data: x
        },
        series: [{
            data: y
        }]
    });
}