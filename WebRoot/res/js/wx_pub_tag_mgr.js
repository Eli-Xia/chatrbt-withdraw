var TagMgrVue = new Vue({
    el: '#tag-mgr',
    data: {
        pubTagList: [],
        modalEditTag: {
            title: '新增标签',
            errInfo: '',
        },
        modalOpType: 'add',
        modalDelItem: {
            id: null,
            confirmInfo: "确认删除标签？"
        },
    },
    created: function() {
        var _this = this;
        $.extend(SaimiriPagination.defaults,{curPage: 1, total: 0, pageSize:20});
        this.querySource(1);
        $("#tag-mgr").on("click", "#page button", function (e) {
            var text = e.target.innerHTML;
            if( Number(text) ){
                if( SaimiriPagination.defaults.curPage != Number(text) ) {
                    _this.querySource(Number(text));
                }
            }
        });
    },
    methods: {
        // 获取标签列表
        querySource: function(page) {
            var _this = this;
            $mks.syncJsonPost({
                data: {
                    page: page,
                    pageSize: SaimiriPagination.defaults.pageSize,
                },
                url: apibase + '/admin/wxPub-tag/page/list',
                success_func: function(res) {
                    if(res.retCode == RetCode.SUCCESS) {
                        _this.pubTagList = res.result;
                        $.extend(SaimiriPagination.defaults, {
                            curPage: page,
                            total: res.total,
                            pageCount: Math.ceil(res.total / SaimiriPagination.defaults.pageSize),
                        });
                        $("#page").html(SaimiriPagination.paging());
                    } else {
                        if(res.retMsg == '用户未登录。') {
                            location.replace(base+'/admin/login.html') ;
                        } else {
                            alert(res.retMsg);
                        }
                    }
                }
            })
        },
        //添加标签
        onAddTag: function() {
            this.clearModal();
            this.modalOpType = 'add';
            this.modalEditTag.title = "添加标签";
            $('#modal_edit_tag').modal('show');
        },
        //修改标签
        onEditTag: function(source) {
            var _this = this;
            this.clearModal();
            // console.log(item)
            this.modalEditTag = Object.assign({}, source, {errInfo: ''});
            this.modalOpType = 'edit';
            this.modalEditTag.title = "修改标签";
            $('#modal_edit_tag').modal('show');
        },
        //添加标签
        addTag: function() {
            var _this = this;
            var data = {
                name: this.modalEditTag.name.trim(),
            };
            $mks.syncJsonPost({
                url: apibase + '/admin/wxPub-tag/add',
                data: data,
                success_func: function(res) {
                    if(res.retCode == RetCode.SUCCESS) {
                        $('#modal_edit_tag').modal('hide');
                        _this.querySource(1);
                    } else {
                        _this.modalShowError( res.retMsg );
                    }
                }
            });
        },
        //修改标签
        editTag: function(id) {
            var _this = this;
            var data = {
                name: this.modalEditTag.name.trim(),
            };
            $mks.syncJsonPost({
                url: apibase + '/admin/wxPub-tag/update/' + id,
                data: data,
                success_func: function(res) {
                    if(res.retCode == RetCode.SUCCESS) {
                        $('#modal_edit_tag').modal('hide');
                        _this.querySource(1);
                    } else {
                        _this.modalShowError( res.retMsg );
                    }
                }
            });
        },
        //删除标签
        deleteTag: function(id) {
            $('#modal_del_item').modal('show');
            this.modalDelItem.id = id;            
        },
        onDelItemConfirm:function(){
            var _self = this;
            $mks.syncJsonPost({
                url: apibase + '/admin/wxPub-tag/delete/' + _self.modalDelItem.id,
                success_func: function(res){
                    if (res.retCode == RetCode.SUCCESS){
                        $('#modal_del_item').modal('hide');
                        _self.querySource(1);
                    } else {
                        alert( res.retMsg );
                        if (res.retCode == RetCode.NO_LOGIN) {
                            location.href = base + '/admin/login.html';
                        }
                    }
                }
            });
        },
        //保存
        saveHandler: function() {
            if( this.modalEditTag.name.trim() == '' ) {
                this.modalShowError("请填写标签名！");
                return;
            }
            if(this.modalOpType =='add') {
                this.addTag();
            }
            if(this.modalOpType =='edit') {
                this.editTag(this.modalEditTag.id);
            }
        },
        blurHandler: function(event) {
            var _this = this;
            var el = event.currentTarget;
            if( $mks.strIsEmpty(el.value) ) {
                el.parentNode.classList.add('has-error');
                _this.modalShowError("请填入标签名");
            } else {
                el.parentNode.classList.remove('has-error');
                el.parentNode.classList.add('has-success');
                _this.modalShowError("");
            }
        },
        //错误提示
        modalShowError: function(info){
            this.modalEditTag.errInfo = info;
        },
        //清空modal
        clearModal: function() {
            $('.has-feedback').removeClass('has-success has-error');
            this.modalEditTag = {
                title:'',
                errInfo: '',
            }
        },
        //向前翻页
        prevPage: function(){
            if (SaimiriPagination.defaults.curPage - 1 > 0) {
                SaimiriPagination.defaults.curPage -= 1;
                this.querySource(SaimiriPagination.defaults.curPage);
            }
        },
        //向后翻页
        nextPage: function(){
            if (SaimiriPagination.defaults.curPage +1 <=SaimiriPagination.defaults.pageCount){
                SaimiriPagination.defaults.curPage += 1;
                this.querySource(SaimiriPagination.defaults.curPage);
            }
        },
    }
})