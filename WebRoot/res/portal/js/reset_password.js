var Alter=new Vue({
    el:"#center",
    data:{
        password:"",
        passwordCopy:"",
        reg:/^(?![0-9]+$)(?![a-zA-Z]+$)[a-zA-Z0-9_`~!@#$%^&*+=()<>{};:,."\/'[\]-]{6,16}$/,//必须要有数字和字母   可以有 特殊字符
        activeCode:"",
        show:false
    },
    created:function () {
        var i=location.pathname.lastIndexOf("/");
        this.activeCode=location.pathname.slice(i+1);
        $("input").on("focus",function () {
            $(this).siblings("b").hide();
        });
    },
    methods:{
        blur:function () {
            if(this.reg.test(this.password)==true){
                $("#password").siblings("b").show().addClass("success").removeClass("fail");
            }else{
                $("#password").siblings("b").show().addClass("fail").removeClass("success");
            }
        },
        copyReg:function () {
            if(this.passwordCopy==this.password && $.trim(this.passwordCopy)!=''){
                $("#password_copy").siblings("b").show().addClass("success").removeClass("fail");
            }else{
                $("#password_copy").siblings("b").show().addClass("fail").removeClass("success");
            }
        },
        alterClick:function () {
            var data={};
            data.activeCode=this.activeCode;
            if(this.reg.test(this.password)){
                if(this.passwordCopy==this.password){
                    data.newPassword=this.password
                }else{
                    alert("两次密码不相同！请确认密码一样！");
                    return;
                }
            }else{
                alert("密码格式不正确！");
                return;
            }
            var dataJson=JSON.stringify(data);
            $.ajax({
                url:apibase+"/user/password/reset",
                data: dataJson,
                type: "post",
                dataType: "json",
                contentType: "application/json;charset=UTF-8",
                async: false,
                success: function (res) {
                    if (res.retCode == RetCode.SUCCESS) {
                        location.replace(base+"/reset_success.html");
                    } else {
                        alert(res.retMsg);
                    }
                }
            })
        }
    }
})