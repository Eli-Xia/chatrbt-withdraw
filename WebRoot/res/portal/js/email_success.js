var emailSuccess=new Vue({
    el:"#center",
    data:{
        email:""
    },
    created:function () {
        var email=sessionStorage.getItem("userEmail");
        this.email=email.split("");
        this.email.splice(4,4,["****"]);
        this.email=this.email.join("");
    },
    methods:{
        againSend:function () {
            var data = {};
            data.email=sessionStorage.getItem("userEmail");
            var dataJson = JSON.stringify(data);
            $.ajax({
                url: apibase + '/user/active/email/resend',
                data: dataJson,
                type: "post",
                dataType: "json",
                contentType: "application/json;charset=UTF-8",
                async: false,
                success: function (res) {
                    if (res.retCode == RetCode.SUCCESS) {
                        alert("邮箱重新发送成功！请去邮箱查收！")
                    } else {
                    }
                }
            })
        }
    }
})