( function(){
    var Login = new Vue({
        el: '#login_form',
        data: {
            username: '',
            password: ''
        },
        created: function(){
            var _self = this;
             $mks.syncJsonGet({
                 url: apibase+'/admin/user/login/stat',
                 success_func: function(response){
                     if (response.result){
                         location.href = base +'/admin/index.html'
                     }
                 }
             });
            $(document).on("keydown", function (e) {
                if (e.keyCode == 13) {
                    _self.btnLoginClick();
                }
            })
        },
        methods: {
            /**
             * 点击登录按钮响应
             */
            btnLoginClick: function(){
                var _self=this;
                if ( this.username.trim() == '' ){
                    $("[type='text']").focus();
                    return;
                }else if ( this.password.trim() == '' ){
                    $("[type='password']").focus();
                    return;
                }
                var reqData = {};
                reqData.username = this.username.trim();
                reqData.password = this.password.trim();
                $mks.syncJsonPost({
                    url: apibase + '/admin/user/login?redirectUri=',
                    data: reqData,
                    success_func: function(response){
                        if (response.retCode == RetCode.SUCCESS){
                            //登录成功
                            localStorage.setItem('legalPersonId',response.result.legalPersonId);
                            localStorage.setItem('userRoles',response.result.roles);
                            window.location.href = base + '/admin/index.html';
                        } else {
                            console.log('Err:' + response.retCode + ' ' + response.retMsg);
                            $("#err_msg").show().html(response.retMsg);
                        }
                    }
                });
            },
            register: function(){
                alert("注册功能未实现");
            }
        }
    })
    //如果没有选择记住登录状态
    $('input').on('ifUnchecked', function(event){
        Login.$data.isCheck = false;
//		alert(Login.$data.isCheck);
    });
    //如果选择了记住登录状态
    $('input').on('ifChecked', function(event){
        Login.$data.isCheck = true;
//		alert(Login.$data.isCheck);
    });
}())