var SaimiriPagination = {
    defaults: {
        curPage: 1,
        total: 0,//总条数
        pageSize: 10,//一页多少条
        pageCount: ""//总页数
    },
    //分页
    paging: function () {
        var _self = this;
        var html = "";
        if (_self.defaults.pageCount - _self.defaults.curPage < 6) {
            for (var i = _self.defaults.pageCount - 6; i <= _self.defaults.pageCount; i++) {
                if (i > 0) {
                    if (i == _self.defaults.curPage) {
                        html += "<button class='page-active'>" + i + "</button>"
                    } else {
                        html += "<button>" + i + "</button>"
                    }
                }
            }
        } else {
            for (var i = 0; i <= 4; i++) {
                if (_self.defaults.curPage == 1) {
                    var p = Number(_self.defaults.curPage) + Number(i);
                    if (p == _self.defaults.curPage) {
                        html += "<button class='page-active'>" + p + "</button>"
                    } else {
                        html += "<button>" + p + "</button>"
                    }
                } else {
                    var p = Number(_self.defaults.curPage) + Number(i) - 1;
                    if (p == _self.defaults.curPage) {
                        html += "<button class='page-active'>" + p + "</button>"
                    } else {
                        html += "<button>" + p + "</button>"
                    }
                }
            }
            html += "<button>" + "···" + "</button>" + "<button>" + ((_self.defaults.pageCount) - 1) + "</button>" + "<button>" + _self.defaults.pageCount + "</button>"
        }
        return html
    },
    //前端 分页
    webPaging: function (page, pages) {
        var html = "";
        if (pages - page < 6) {
            for (var i = pages - 6; i <= pages; i++) {
                if (i > 0) {
                    if (i == page) {
                        html += "<button class='active'>" + i + "</button>"
                    } else {
                        html += "<button>" + i + "</button>"
                    }
                }
            }
        } else {
            for (var i = 0; i <= 2; i++) {
                if (page == 1) {
                    var p = Number(page) + Number(i);
                    if (p == page) {
                        html += "<button class='active'>" + p + "</button>"
                    } else {
                        html += "<button>" + p + "</button>"
                    }
                } else {
                    var p = Number(page) + Number(i) - 1;
                    if (p == page) {
                        html += "<button class='active'>" + p + "</button>"
                    } else {
                        html += "<button>" + p + "</button>"
                    }
                }
            }
            html += "<button>" + "···" + "</button>" + "<button>" + ((pages) - 1) + "</button>" + "<button>" + pages + "</button>"
        }
        return html;
    }
};