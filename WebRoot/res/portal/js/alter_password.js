var Alter=new Vue({
    el:"#center",
    data:{
        password:"",
        passwordCopy:"",
        reg:/^(?![0-9]+$)(?![a-zA-Z]+$)\w{6,16}$/,//必须要有数字和字母   可以有 特殊字符
    },
    created:function () {

    },
    methods:{
        alterClick:function () {
            var data={};
            if(this.reg.test(this.password)){
                if(this.passwordCopy==this.password){
                    data.password=this.password
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
                url:apibase+"/user/password/update",
                data: dataJson,
                type: "post",
                dataType: "json",
                contentType: "application/json;charset=UTF-8",
                async: false,
                success: function (res) {
                    if (res.retCode == RetCode.SUCCESS) {
                        location.replace(base+"/alter_success.html");
                    } else {

                    }
                }
            })
        }
    }
})