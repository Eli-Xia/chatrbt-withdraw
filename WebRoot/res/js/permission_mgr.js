var pmsMgrVue = new Vue({
    el: '#permission-mgr',
    data: {
        sourceList: [],
        roleList: [],
        currentRoles: [],
        modalEditPms: {
            title: '新增资源',
            allowRoles: [],
            errInfo: '',
        },
        modalOpType: 'add',
        modalDelItem: {
            id: null,
            confirmInfo: "确认删除资源？"
        },
    },
    created: function() {
        var _this = this;
        $.extend(SaimiriPagination.defaults,{curPage: 1, total: 0, pageSize:20});
        this.querySource(1);
        this.queryRole();
        $("#permission-mgr").on("click","#page button",function (e) {
            var text = e.target.innerHTML;
            if( Number(text) ){
                if( SaimiriPagination.defaults.curPage != Number(text) ) {
                    _this.querySource(Number(text));
                }
            }
        });
    },
    methods: {
        // 获取资源列表
        querySource: function(page) {
            var _this = this;
            $mks.syncJsonPost({
                data: {
                    page: page,
                    pageSize: SaimiriPagination.defaults.pageSize,
                },
                url: apibase + '/admin/permission/list',
                success_func: function(res) {
                    if(res.retCode == RetCode.SUCCESS) {
                        _this.sourceList = res.result;
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
        // 获取所有角色
        queryRole: function() {
            var _this = this;
            $mks.syncJsonPost({
                data: {},
                url: apibase + '/admin/user/role/list',
                success_func: function(res) {
                    if(res.retCode == RetCode.SUCCESS) {
                        res.result.map(function(role) {
                            role.active = false;
                        })
                        _this.roleList = res.result;
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
        //添加资源
        onAddPermission: function() {
            this.clearModal();
            this.modalOpType = 'add';
            this.modalEditPms.title = "添加资源";
            $('#modal_edit_pms').modal('show');
        },
        //修改资源
        onEditPermission: function(source) {
            var _this = this;
            this.clearModal();
            this.modalEditPms = Object.assign({}, source, {errInfo: ''});
            // this.currentRoles = source.allowRoles.slice(0);
            var tempRoles = source.allowRoles.slice(0);
                tempRoles.map(function(item) {
                    _this.roleList.map(function(role) {
                        if (item === role.name) {
                            if (_this.currentRoles.indexOf(role.code) < 0) {
                                _this.currentRoles.push(role.code)
                            }
                        }
                    })
                })
            this.modalOpType = 'edit';
            this.modalEditPms.title = "修改资源";
            $('#modal_edit_pms').modal('show');
        },
        //添加资源
        addPsm: function() {
            var _this = this;
            var data = {
                name: this.modalEditPms.name.trim(),
                res: this.modalEditPms.res.trim(),
                allowRoles: this.currentRoles,
                description: this.modalEditPms.description,
            };
            $mks.syncJsonPost({
                url: apibase + '/admin/permission/add',
                data: data,
                success_func: function(res) {
                    if(res.retCode == RetCode.SUCCESS) {
                        $('#modal_edit_pms').modal('hide');
                        _this.querySource(1);
                    } else {
                        _this.modalShowError( res.retMsg );
                    }
                }
            });
        },
        //修改资源
        editPsm: function(id) {
            var _this = this;
            this.currentRoles = this.currentRoles.filter(function(item) {
                return item != '';
            })
            var data = {
                name: this.modalEditPms.name.trim(),
                res: this.modalEditPms.res.trim(),
                allowRoles: this.currentRoles,
                description: this.modalEditPms.description,
            };
            $mks.syncJsonPost({
                url: apibase + '/admin/permission/' + id + '/update',
                data: data,
                success_func: function(res) {
                    if(res.retCode == RetCode.SUCCESS) {
                        $('#modal_edit_pms').modal('hide');
                        _this.querySource(SaimiriPagination.defaults.curPage);
                    } else {
                        _this.modalShowError( res.retMsg );
                    }
                }
            });
        },
        //删除资源
        deletePsm: function(id) {
            $('#modal_del_item').modal('show');
            this.modalDelItem.id = id;            
        },
        onDelItemConfirm:function(){
            var _self = this;
            $mks.syncJsonPost({
                url: apibase + '/admin/permission/delete/' + _self.modalDelItem.id,
                success_func: function(res){
                    if (res.retCode == RetCode.SUCCESS){
                        $('#modal_del_item').modal('hide');
                        _self.querySource(1);
                    } else {
                        alert( reps.retMsg );
                    }
                }
            });
        },
        //保存
        saveHandler: function() {
            if( this.currentRoles.length < 1 ) {
                this.modalShowError("必须至少选择一个角色");
                return;
            }
            if(this.modalOpType =='add') {
                this.addPsm();
            }
            if(this.modalOpType =='edit') {
                this.editPsm(this.modalEditPms.id);
            }
        },
        blurHandler: function(event) {
            var _this = this;
            var el = event.currentTarget;
            if( $mks.strIsEmpty(el.value) ) {
                el.parentNode.classList.add('has-error');
                _this.modalShowError("请填入有效信息");
            } else {
                el.parentNode.classList.remove('has-error');
                el.parentNode.classList.add('has-success');
                _this.modalShowError("");
            }
        },
        //错误提示
        modalShowError: function(info){
            this.modalEditPms.errInfo = info;
        },
        //清空modal
        clearModal: function() {
            $('.has-feedback').removeClass('has-success has-error');
            this.currentRoles = [];
            this.modalEditPms = {
                title:'',
                allowRoles: [],
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