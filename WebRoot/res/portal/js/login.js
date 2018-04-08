var Login = new Vue({
    el: '#login',
    data: {
        email: '',
        password: '',
        remember: false,
        active: "remember-sure"
    },
    created: function () {
        var _self = this;
        if (localStorage.getItem("remember")) {
            var holdTime = localStorage.getItem("holdTime");
            var nowTime=new Date().getTime();
            var saveTime=nowTime-holdTime;
            if(saveTime<86400000*3){
                _self.password = localStorage.getItem("password");
                _self.email = localStorage.getItem("email");
                _self.remember = localStorage.getItem("remember");
            }else{
                localStorage.removeItem("email");
                localStorage.removeItem("password");
                localStorage.removeItem("remember");
                localStorage.removeItem("holdTime");
            }
        };
    },
    methods: {
        loginClick: function () {
            var data = {};
            var _self = this;
            if ($.trim(this.email) == '') {
                alert('用户名不能为空');
                $("[type='text']").focus();
                return;
            } else if ($.trim(this.password) == '') {
                alert('密码不能为空');
                $("[type='password']").focus();
                return;
            }
            ;
            data.email = $.trim(this.email);
            data.password = $.trim(this.password);
            var dataJson = JSON.stringify(data);
            $.ajax({
                url: apibase + '/user/login',
                data: dataJson,
                type: "post",
                dataType: "json",
                contentType: "application/json;charset=UTF-8",
                async: false,
                success: function (res) {
                    if (res.retCode == RetCode.SUCCESS) {
                        if (_self.remember) {
                            //记住密码
                            if(!localStorage.getItem("remember")){
                                var nowTime=new Date().getTime();
                                localStorage.setItem("email", data.email);
                                localStorage.setItem("password", data.password);
                                localStorage.setItem("remember", true);
                                localStorage.setItem("holdTime", nowTime);
                            }
                        } else {
                            localStorage.removeItem("email");
                            localStorage.removeItem("password");
                            localStorage.removeItem("remember");
                            localStorage.removeItem("holdTime");
                        }
                        window.location.href = base + '/home.html';
                    } else {
                        $("#err_msg").show();
                    }
                }
            })
        },
        registerClick: function () {
            window.location.href = base + '/register.html';
        }
    }
});
$(function () {
    var height=$("body").height();
    var h=$(".large").height();
    if(height-h>0){
        $(".large").css({bottom:-10,top:"auto"})
    }else{
        $(".large").css({top:10,bottom:"auto"})
    }
    $(window).resize(function(){
        var height=$("body").height();
        var h=$(".large").height();
        if(height-h>=0){
            $(".large").css({bottom:-10,top:"auto"})
        }else{
            $(".large").css({top:10,bottom:"auto"})
        }
    });
});