var Find=new Vue({
    el:"#center",
    data:{
        email:"",
        regEmail : /^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(.[a-zA-Z0-9_-])+/,//邮箱格式
    },
    methods:{
        sendClick:function () {
            var _self=this;
            var data={};
            if(_self.regEmail.test(_self.email)){
                data.email=_self.email;
            }else{
                alert("邮箱格式不正确！");
                $("#email").focus();
                return;
            }
            var dataJson=JSON.stringify(data);
            $.ajax({
                url:apibase+"/user/password/retrieve",
                data: dataJson,
                type: "post",
                dataType: "json",
                contentType: "application/json;charset=UTF-8",
                async: false,
                success: function (res) {
                    if (res.retCode == RetCode.SUCCESS) {
                        sessionStorage.setItem("userEmail",data.email);
                        location.href = base+"/account_email.html"
                    } else {
                        alert(res.retMsg);
                    }
                }
            })
        }
    }
})