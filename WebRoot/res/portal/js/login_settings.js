var Menage=new Vue({
    el:"#login_settings",
    data:{
        curPassword:"",
        newPassword:"",
        newPasswordCopy:"",
        disabled:true,
        active:"active",
        hide:false
    },
    created:function () {

    },
    methods:{
        surePassword:function () {
            var _self=this;
            var data={};
            if(_self.curPassword.trim()==''){
                dialog("请填写当前密码!",2);
                $("#cur_password").focus();
                return;
            }else{
                data.curPassword=_self.curPassword
            };
            if(_self.newPassword.trim()==''){
                dialog("请填写新密码!",2);
                $("#new_password").focus();
                return;
            }else{
                data.newPassword=_self.newPassword
            };
            if(_self.newPasswordCopy!=_self.newPassword){
                dialog("新密码两次填写不一致!",2);
                $("#password_copy").focus();
                return;
            };
            $mks.syncJsonPost({
                url:apibase+"/user/password/update",
                data:data,
                success_func:function (res) {
                    if(res.retCode==RetCode.SUCCESS){
                        _self.curPassword='';
                        _self.newPassword='';
                        _self.newPasswordCopy='';
                        _self.disabledPs=true;
                        dialog("修改密码成功!",1)
                    }else{
                        dialog(res.retMsg,2)
                    }
                }
            })
        },
        disToggle:function () {
            this.disabled=!this.disabled
        }
    }
});