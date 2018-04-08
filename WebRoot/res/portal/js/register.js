var Register = new Vue({
    el: '#center',
    data: {
        password: '',
        email: "",
        nickname: "",
        passwordCopy: "",
        code: "",
        regEmail: /^([a-zA-Z0-9_.-])+@([a-zA-Z0-9_-])+(.[a-zA-Z0-9_-])+/,//邮箱格式
        regNick: /^[\u4e00-\u9fa5a-zA-Z0-9_-]{1,30}$/,
        regPassword: /^(?![0-9]+$)(?![a-zA-Z]+$)[a-zA-Z0-9_`~!@#$%^&*+=()<>{};:,."\/'[\]-]{6,16}$/,//必须要有数字和字母   可以有 特殊字符
        msg: "",
        show: false,
    },
    created: function () {
        var _self = this;
        this.requireCode();
        $("input").on("focus", function () {
            $(this).siblings(".tip").show().siblings("b").hide();
        });
        $("#code").on('blur', function () {
            $(this).siblings(".tip").hide();
        })
    },
    methods: {
        /**
         * 二维码
         */
        requireCode: function () {
            $("#codeImg").attr("src", base + "/api/verification-code.jpg?" + Math.random())
        },
        blur: function (el, val, reg) {
            $("#" + el).siblings(".tip").hide();
            if (reg.test(val) == true) {
                $("#" + el).siblings("b").show().addClass("success").removeClass("fail");
            } else {
                $("#" + el).siblings("b").show().addClass("fail").removeClass("success");
            }
        },
        copyReg: function (el) {
            $("#" + el).siblings(".tip").hide();
            if (this.passwordCopy == this.password && $.trim(this.passwordCopy) != '') {
                $("#" + el).siblings("b").show().addClass("success").removeClass("fail");
            } else {
                $("#" + el).siblings("b").show().addClass("fail").removeClass("success");
            }
        },
        registerClick: function () {
            var _self = this;
            var data = {};
            if (this.regEmail.test(this.email)) {
                data.email = this.email;
            } else {
                $("#user_email").focus();
                return;
            }
            if (this.regNick.test(this.nickname)) {
                data.nickname = this.nickname;
            } else {
                $("#nickname").focus();
                return;
            }
            if (this.regPassword.test(this.password)) {
                data.password = this.password;
            } else {
                $("#password").focus();
                return;
            }
            if (this.passwordCopy == this.password) {

            } else {
                $("#password_clone").focus();
                return;
            }
            if (this.code.trim() == '') {
                $("#code").focus();
                alert("验证码不能为空！");
                return;
            } else {
                data.capText = this.code;
            }
            var dataJson = JSON.stringify(data);
            $.ajax({
                url: apibase + '/user/register',
                data: dataJson,
                type: "post",
                dataType: "json",
                contentType: "application/json;charset=UTF-8",
                async: false,
                success: function (res) {
                    if (res.retCode == RetCode.SUCCESS) {
                        sessionStorage.setItem("userEmail", data.email);
                        _self.show = false;
                        location.replace(base + "/email_success.html");
                    } else if (res.retCode == RetCode.FAILED) {
                        _self.requireCode();
                        _self.show = true;
                        _self.msg = res.retMsg
                    }
                }
            })
        }
    }
});
$(function () {
    var height = $("body").height();
    var h = $(".large").height();
    if (height - h > 0) {
        $(".large").css({bottom:-10,top:"auto"})
    } else {
        $(".large").css({top:10,bottom:"auto"})
    }
    $(window).resize(function () {
        var height = $("body").height();
        var h = $(".large").height();
        if (height - h >= 0) {
            $(".large").css({bottom:-10,top:"auto"})
        } else {
            $(".large").css({top:10,bottom:"auto"})
        }
    });
});