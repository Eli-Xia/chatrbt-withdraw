new Vue({
    el: '#data_count',
    data: {
        beginDate: '',
        endDate: '',
        maxDate: '',
        dateActive: 0,
        list: {},
        yesterdayMen:'--',
        yesterdayNum:'--',
        historySign:false
    },
    created: function () {
        this.intTime(1);
    },
    methods: {
        queryList: function () {
            var _self=this;
            $mks.syncJsonPost({
                url: apibase + "/admin/chatlog/statistic/chatManAndNum/list",
                data: {
                    beginDate: this.beginDate,
                    endDate: this.endDate
                },
                success_func: function (resp) {
                    if (resp.retCode == RetCode.SUCCESS) {
                        var xDate = [], men = [], num = [],yLength = 0;
                        $.each(resp.result, function (i, n) {
                            xDate.push($mks.formatDateTime(n.createtime));
                            men.push(n.totalchatman);
                            num.push(n.totalchatnum);
                            var l=n.totalchatnum.toString().length/3;
                            if (l> yLength) {
                                yLength = Math.ceil(l);
                            }
                        });
                        if(_self.historySign==false){
                            _self.yesterdayMen=men[men.length-1];
                            _self.yesterdayNum=num[num.length-1];
                            _self.historySign=true
                        }
                        myChart.setOption({
                            grid: {
                                x:30*yLength
                            },
                            xAxis: {
                                data: xDate
                            },
                            series: [
                                {
                                    data: num
                                },
                                {
                                    data:men
                                }
                            ]
                        });
                    } else {
                        alert(resp.retMsg);
                        if (resp.retCode == RetCode.NO_LOGIN) {
                            location.href = base + '/admin/login.html';
                        }
                    }
                }
            })
        },
        intTime: function (key) {
            var _self = this;
            var date = new Date().getTime();
            this.maxDate = $mks.formatDateTime(date);
            this.dateActive = key;
            if (key == 1) {
                this.endDate = $mks.formatDateTime(date);
                this.beginDate = $mks.formatDateTime(date - (86400000 * 6));
            } else {
                this.endDate = $mks.formatDateTime(date);
                this.beginDate = $mks.formatDateTime(date - (86400000 * 29));
            }
            this.queryList();
            $('#chat_time').daterangepicker({
                "startDate": _self.beginDate,
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
                _self.dateActive = 3;
                _self.beginDate = $mks.formatDateTime(start);
                _self.endDate = $mks.formatDateTime(end);
                _self.queryList();
            });
        },
    }
});
$(function () {
    myChart = echarts.init(document.getElementById("chart_num"));
    option = {
        xAxis: {
            type: 'category',
            axisTick: {
                show: false,
            },
            axisLabel: {interval: 'auto'},
            data: ''
        },
        legend: {
            data:['聊天总次数','聊天总人数']
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
            }
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
            x: 0,
            y: 30,
            x2: 30,
            y2: 30,
        },
        series: [
            {
                data: '',
                type: 'line',
                name: '聊天总次数',
                symbol: "circle",
                symbolSize: 6,
                showAllSymbol: true,
                itemStyle: {
                    normal: {
                        color: '#ffd553',
                        borderWidth: 2,
                        borderColor: "#fff",
                        lineStyle: {
                            color: '#ffd553'
                        }
                    }
                }
            },
            {
                data: '',
                type: 'line',
                name: '聊天总人数',
                symbol: "circle",
                symbolSize: 6,
                showAllSymbol: true,
                itemStyle: {
                    normal: {
                        color: '#28a7fd',
                        borderWidth: 2,
                        borderColor: "#fff",
                        lineStyle: {
                            color: '#28a7fd'
                        }
                    }
                }
            }
        ]
    };
    myChart.setOption(option);
});