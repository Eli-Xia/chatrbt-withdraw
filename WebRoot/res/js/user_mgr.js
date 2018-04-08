var userMgrVue = new Vue({
    el: '#user_mgr',
    data:{
        list:[],
        page:1,
        modalOpType:"add",//add,edit
        modalEditUser:{
            title:"添加用户",
            userId:null,
            username:'',
            nickname:'',
            password:'',
            password2:'',
            errInfo:'',
            roles: [],
        },
        modalDelItem:{
            itemId:null,
            confirmInfo:"删除用户将同时解除公众号管理员关系，确认删除？"
        },
        roleList: [],
    },
    created:function(){
        var _self=this;
        $.extend(SaimiriPagination.defaults,{curPage: 1, total: 0, pageSize:20});
        _self.queryList(1);
        _self.queryRole();
        $("#user_mgr").on("click","#page button",function (e) {
            var text=e.target.innerHTML;
            if(Number(text)){
                if(SaimiriPagination.defaults.curPage!=Number(text)){
                    _self.queryList(text);
                }
            }
        });
    },
    methods:{
        /**
         * 获取用户列表
         */
        queryList:function(page){
            var _self=this;
            var page=page;

            $mks.syncJsonPost({
                data:{
                    page:page,
                    pageSize:SaimiriPagination.defaults.pageSize
                },
                url:apibase + '/admin/user/list',
                success_func: function(resp){
                    if ( resp.retCode == RetCode.SUCCESS){
                        _self.list=resp.result;
                        $.extend(SaimiriPagination.defaults,{curPage:page, total:resp.total, pageCount:Math.ceil(resp.total/SaimiriPagination.defaults.pageSize)});
                        $("#page").html(SaimiriPagination.paging());
                    }else{
                        alert(resp.retMsg);
                        if (resp.retCode == RetCode.NO_LOGIN) {
                            location.href = base + '/admin/login.html';
                        }
                    }
                }
            })
        },
         /**
         * 获取所有角色
         */
        queryRole: function() {
            var _self = this;
            $mks.syncJsonPost({
                data: {},
                url: apibase + '/admin/user/role/list',
                success_func: function(res) {
                    if(res.retCode == RetCode.SUCCESS) {
                        res.result.map(function(role) {
                            role.active = false;
                        })
                        _self.roleList = res.result;
                    } else {
                        alert(res.retMsg);
                        if (res.retCode == RetCode.NO_LOGIN) {
                            location.href = base + '/admin/login.html';
                        }
                    }
                }
            })
        },
        /**
         * 向前翻页
         */
        lastPage: function(){
            if (SaimiriPagination.defaults.curPage - 1 > 0) {
                SaimiriPagination.defaults.curPage -= 1;
                this.queryList(SaimiriPagination.defaults.curPage);
            }
        },
        /**
         * 向后翻页
         */
        nextPage: function(){
            if (SaimiriPagination.defaults.curPage +1 <=SaimiriPagination.defaults.pageCount){
                SaimiriPagination.defaults.curPage += 1;
                this.queryList(SaimiriPagination.defaults.curPage);
            }
        },
        /**
         * 点击添加按钮响应
         */
        onBtnAdd: function(){
            this.modalOpType = 'add';
            this.clearModalEditUser();
            this.modalEditUser.title = "添加用户";
            this.modalEditUser.roles = [];
            //初始化rolelist-active
            this.roleList.map(function(role) {
                role.active = false;
            })
            $('#modal_edit_user').modal('show');
        },
        /**
         * 点击编辑用户按钮响应
         * @param {Object} userId
         */
        onBtnEdit: function(userId){

            var _self = this;

            this.modalOpType = 'edit';
            this.clearModalEditUser();
            this.modalEditUser.title = "编辑用户";
            this.modalEditUser.userId = userId;
            $('#modal_edit_user').modal('show');
            //初始化rolelist-active
            _self.roleList.map(function(role) {
                role.active = false;
            })

            $mks.syncJsonPost({
                url: apibase + '/admin/user/' + userId + '/detail',
                data: {},
                success_func: function(resp){
                    if (resp.retCode == RetCode.SUCCESS){
                        var userDetail = resp.result;
                        _self.modalEditUser.username = userDetail.username;
                        _self.modalEditUser.nickname = userDetail.nickname;
                        _self.modalEditUser.roles = userDetail.roles;
                        
                        //已有role高亮显示
                        _self.modalEditUser.roles.forEach(function(role) {
                            _self.roleList.map(function(item) {
                                if(item.code.toUpperCase() == role.toUpperCase()) {
                                    item.active = true;
                                }
                            })
                        });

                    } else {
                        alert( resp.retMsg );
                        if (resp.retCode == RetCode.NO_LOGIN){
                            location.href = base + '/admin/login.html';
                        }
                    }
                }
            });
        },
        //选择角色标签
        selectRole: function(code) {
            var _self = this;
            var idx = this.modalEditUser.roles.indexOf(code);
            //修改current user role
            if(idx > -1) {
                this.modalEditUser.roles.splice(idx, 1);
            } else {
                this.modalEditUser.roles.push(code);
            }
            _self.roleList.forEach(function(item) {
                item.active = false;
                _self.modalEditUser.roles.map(function(role) {
                    if(item.code.toUpperCase() == role.toUpperCase()) {
                        item.active = true;
                    } 
                })
            });
        },
        /**
         * 点击删除按钮
         * @param {Object} userId
         */
        onBtnDel:function(userId){
            $('#modal_del_item').modal('show');
            this.modalDelItem.itemId = userId
        },
        onDelItemConfirm:function(){

            var _self = this;

            $mks.syncJsonPost({
                url: apibase + '/admin/user/del',
                data: {userId:_self.modalDelItem.itemId},
                success_func: function(resp){
                    if (resp.retCode == RetCode.SUCCESS){
                        _self.queryList(1);
                        $('#modal_del_item').modal('hide');
                    } else {
                        alert( resp.retMsg );
                        if (resp.retCode == RetCode.NO_LOGIN){
                            location.href = base + '/admin/login.html';
                        }
                    }
                }
            });
        },
        /**
         * 清空添加用户／编辑用户对话框
         */
        clearModalEditUser: function(){
            this.modalEditUser = {
                title:'',
                userId:null,
                username:'',
                nickname:'',
                password:'',
                password2:'',
                errInfo:''
            }
        },
        /**
         * 保存用户按钮响应
         */
        onSaveUser:function(){
            if(this.modalOpType == 'add'){
                this.addUser();
            }else if ( this.modalOpType == 'edit'){
                this.updateUser();
            }
        },
        /**
         * 添加用户
         */
        addUser:function(){

            var _self = this;

            if ( $mks.strIsEmpty(this.modalEditUser.username) ){
                _self.modalShowError( "用户名不能为空." );
                return;
            }

            if ( $mks.strIsEmpty(this.modalEditUser.nickname) ){
                _self.modalShowError( "昵称不能为空." );
                return;
            }

            if ( $mks.strIsEmpty(this.modalEditUser.password) ){
                _self.modalShowError( "密码不能为空." );
                return;
            }

            if ( this.modalEditUser.password != this.modalEditUser.password2 ){
                _self.modalShowError( "两次密码输入不一致,请重新输入." );
                return;
            }

            if (this.modalEditUser.roles.length < 1) {
                _self.modalShowError( "请至少选择一个角色." );
                return;
            }

            var reqData = {
                username:this.modalEditUser.username.trim(),
                nickname:this.modalEditUser.nickname.trim(),
                password:this.modalEditUser.password.trim(),
                roles: this.modalEditUser.roles,
            };

            $mks.syncJsonPost({
                url: apibase + '/admin/user/add',
                data: reqData,
                success_func: function(resp){
                    if (resp.retCode == RetCode.SUCCESS){
                        $('#modal_edit_user').modal('hide');
                        _self.queryList(1);
                    } else {
                        _self.modalShowError( resp.retMsg );
                    }
                }
            });
        },
        /**
         * 更新用户
         */
        updateUser:function(){

            var _self = this;

            if ( $mks.strIsEmpty(this.modalEditUser.username) ){
                _self.modalShowError( "用户名不能为空." );
                return;
            }

            if ( $mks.strIsEmpty(this.modalEditUser.nickname) ){
                _self.modalShowError( "昵称不能为空." );
                return;
            }

            if ( ! $mks.strIsEmpty(this.modalEditUser.password) || ! $mks.strIsEmpty(this.modalEditUser.password2) ){
                if ( this.modalEditUser.password != this.modalEditUser.password2 ){
                    _self.modalShowError( "两次密码输入不一致,请重新输入." );
                    return;
                }
            }
            if (this.modalEditUser.roles.length < 1) {
                _self.modalShowError( "请至少选择一个角色." );
                return;
            }

            var reqData = {
                userId:this.modalEditUser.userId,
                username:this.modalEditUser.username.trim(),
                nickname:this.modalEditUser.nickname.trim(),
                roles: this.modalEditUser.roles,
            };

            if ( !$mks.strIsEmpty(this.modalEditUser.password) ){
                reqData.password = this.modalEditUser.password.trim();
            }

            $mks.syncJsonPost({
                url: apibase + '/admin/user/update',
                data: reqData,
                success_func: function(resp){
                    if (resp.retCode == RetCode.SUCCESS){
                        $('#modal_edit_user').modal('hide');
                        _self.queryList(1);
                    } else {
                        _self.modalShowError( resp.retMsg );
                    }
                }
            });
        },
        /**
         * 对话框现示错误提示
         * @param {Object} info
         */
        modalShowError:function(info){
            this.modalEditUser.errInfo = info;
        },
    },
    filters: {
        formateTimeStamp: function(time){
            return $mks.formatUnixTimeStamp(time);
        }
    }
})