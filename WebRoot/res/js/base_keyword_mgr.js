new Vue({
    el: "#key_mgr",
    data: {
        keyList: {},
        condition: "",
        keyword: "",
        replyContent: "",
        detailIndex: 0,
        alterShow: false,
        detailShow: false,
        addShow: false,
        searchKeyWord: '',
        showLoading: false,
        resetActive:false
    },
    created: function () {
        var _self=this;
        $.extend(SaimiriPagination.defaults, {curPage: 1, total: 0, pageSize: 10});
        this.queryList(1);
        $("#key_mgr").on("click", ".page button", function (e) {
            var text = e.target.innerHTML;
            if (Number(text)) {
                if(SaimiriPagination.defaults.curPage == Number(text)){
                    return;
                }
                _self.queryList(Number(text));
            }else if (text == "首页") {
                if (SaimiriPagination.defaults.curPage != 1) {
                    _self.queryList(1);
                }
            } else if (text == "尾页") {
                if (SaimiriPagination.defaults.curPage != SaimiriPagination.defaults.pageCount) {
                    _self.queryList(SaimiriPagination.defaults.pageCount);
                }
            }
        })
    },
    methods: {
        queryList: function (page) {
            var _self = this;
            if($.trim(_self.searchKeyWord)==''){
                _self.resetActive=true;
            }else{
                _self.resetActive=false;
            };
            $mks.syncJsonPost({
                url: apibase + "/admin/kr/base/list",
                data: {
                    page: page,
                    pageSize: SaimiriPagination.defaults.pageSize,
                    keywords: _self.searchKeyWord,
                },
                success_func: function (resp) {
                    if (resp.retCode == RetCode.SUCCESS) {
                        _self.keyList = resp.result;
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
         * 详情
         */
        detailModel: function (index) {
            this.detailIndex = index;
            this.int();
            this.detailShow=true;
        },
        hideDetail: function () {
            this.detailShow=false;
        },
        alterModel: function (index) {
            this.detailIndex = index;
            this.int();
            this.alterShow=true;
            this.keyword=this.keyList[index].keywords.join(",");
            this.replyContent=this.keyList[index].response;
        },
        hideAlter: function () {
            this.alterShow=false;
        },
        updateReply: function () {
            var _self=this,data={};
            if(this.keyList[this.detailIndex].keywords.join(",")==this.keyword && this.replyContent==this.keyList[this.detailIndex].response){
                this.alterShow=false;
                return;
            }
            if($.trim(this.keyword)==''){
                alert("关键词不能为空!");
                return;
            }else{
                data.keywords=this.keyword.split();
            }
            if($.trim(this.replyContent)==''){
                alert("回复不能为空!");
                return;
            }else{
                data.response=this.replyContent;
            }
            data.id=this.keyList[this.detailIndex].keywordsId;
            data.rule=1;
            $mks.syncJsonPost({
                url: apibase + "/admin/kr/base/update",
                data: data,
                success_func: function (resp) {
                    if (resp.retCode == RetCode.SUCCESS) {
                        alert("修改成功!");
                        _self.alterShow=false;
                        _self.queryList(SaimiriPagination.defaults.curPage);
                    } else {
                        alert(resp.retMsg);
                        if (resp.retCode == RetCode.NO_LOGIN) {
                            location.href = base + '/admin/login.html';
                        }
                    }
                }
            })
        },
        delReply: function (index,id) {
            var _self=this;
            if(confirm("确定删除关键词 '"+this.keyList[index].keywords+"' 及其回复吗?")){
                $mks.syncJsonPost({
                    url: apibase + "/admin/kr/base/delete/"+id,
                    success_func: function (resp) {
                        if (resp.retCode == RetCode.SUCCESS) {
                            alert("删除成功!");
                            _self.queryList(SaimiriPagination.defaults.curPage);
                        } else {
                            alert(resp.retMsg);
                            if (resp.retCode == RetCode.NO_LOGIN) {
                                location.href = base + '/admin/login.html';
                            }
                        }
                    }
                })
            }
        },
        addModel: function () {
            this.int();
            this.addShow=true;
        },
        hideAdd: function () {
            this.addShow=false;
        },
        saveReply: function () {
            var _self=this,data={};
            if($.trim(this.keyword)==''){
                alert("关键词不能为空!");
                return;
            }else{
                data.keywords=this.keyword.split();
            }
            if($.trim(this.replyContent)==''){
                alert("回复不能为空!");
                return;
            }else{
                data.response=this.replyContent;
            }
            data.rule=1;
            $mks.syncJsonPost({
                url: apibase + "/admin/kr/base/set",
                data: data,
                success_func: function (resp) {
                    if (resp.retCode == RetCode.SUCCESS) {
                        alert("保存成功!");
                        _self.addShow=false;
                        _self.queryList(SaimiriPagination.defaults.curPage);
                    } else {
                        alert(resp.retMsg);
                        if (resp.retCode == RetCode.NO_LOGIN) {
                            location.href = base + '/admin/login.html';
                        }
                    }
                }
            })
        },
        int: function () {
            this.keyword = "";
            this.replyContent = "";
            this.alterShow = false;
            this.detailShow = false;
            this.addShow = false
        },
        //搜索
        resetKey: function() {
            this.searchKeyWord = '';
            if(this.resetActive==false){
                this.queryList(1);
            }
        },
        //上传Excel
        uploadFile: function() {
            var _this = this;
            if(window.FormData) {
                var formData = new FormData();
                // 建立一个upload表单项，值为上传的文件
                formData.append('excelFile', document.getElementById('upload').files[0]);
                var fileType=document.getElementById('upload').files[0].type;
                if(fileType=='application/vnd.ms-excel' || fileType=='application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'){
                    $.ajax({
                        url: '/api/admin/kr/importKwResp',
                        type: 'POST',
                        data: formData,
                        cache: false,
                        processData: false,
                        contentType: false,
                        beforeSend: function() {
                            _this.showLoading = true;
                        },
                        success: function(res) {
                            _this.showLoading = false;
                            if (res.retCode == RetCode.SUCCESS) {
                                alert("上传成功!");
                                _this.queryList(1);
                            } else {
                                alert(res.retMsg);
                                if (res.retCode == RetCode.NO_LOGIN) {
                                    location.href = '/admin/login.html';
                                }
                            }
                        }
                    });
                }else{
                    alert("请上传正确的 excel 文件格式！")
                }

            }
        }
    }
})