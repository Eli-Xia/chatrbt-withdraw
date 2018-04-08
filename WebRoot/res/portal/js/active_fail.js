var emailSuccess=new Vue({
    el:"#center",
    data:{
        email:""
    },
    created:function () {

    },
    methods:{
        againSend:function () {
            alert("发送成功!!");
            var data = {};
            var dataJson = JSON.stringify(data);
            $.ajax({
                url: apibase + '',
                data: dataJson,
                type: "post",
                dataType: "json",
                contentType: "application/json;charset=UTF-8",
                async: false,
                success: function (res) {
                    if (res.retCode == RetCode.SUCCESS) {
                        location.href = base+"/email_success.html"
                    } else {
                    }
                }
            })
        }
    }
})