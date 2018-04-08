$(function () {
    var IndexVue = new Vue({
        el: "#index",
        data: {},
        created: function () {
            var height = $("body").height();
            if (height - 116 > 670) {
                $("#center, aside").css({
                    "min-height": height - 176 + "px"
                });
            }
            ;
            loadMainContent(base + '/home_page.html')
            //获取用户信息
        },
        methods: {
            logout: function () {
                $("#confirm_layer").hide();
                $.ajax({
                    url: apibase + '/user/logout',
                    type: 'GET',
                    async: true,
                    success: function (res) {
                        if (res.retCode == RetCode.SUCCESS) {
                            if (localStorage.getItem("remember")) {
                                localStorage.removeItem("email");
                                localStorage.removeItem("password");
                                localStorage.removeItem("remember");
                                localStorage.removeItem("holdTime");
                            }
                            location.replace(base + '/index.html');
                        }
                    }
                });
            },
            msgHide: function () {
                $("#msg_layer").hide();
                var text = $("#msg_layer .content").html();
                if (text == "用户未登录。") {
                    location.replace(base + '/login.html');
                }
            },
            confirmShow: function (text) {
                $("#confirm_layer").show();
                $("#confirm_content").html(text);
            },
            confirmHide: function () {
                $("#confirm_layer").hide();
            },
            changeTab: function (event, path) {
                var el = event.currentTarget;
                if (el.className.indexOf("disable") < 0) {
                    $("aside .active").removeClass("active");
                    el.classList.add("active");
                    loadMainContent(path);
                } else {
                    dialog("尚未开通!", 2);
                }
            }
        }
    });
});
var loadMainContent = function (path) {
    $("#layer").show();
    $("#center").empty().load(path, function () {
        if (path != '/menage_account.html') {
            $("#layer").hide();
        }
    });

};
var dialog = function (text, key) {
    $("#msg_layer").show();
    $("#msg_layer .content").html(text);
    if (key == 1) {
        $("#msg_layer .success-msg").show();
        $("#msg_layer .err-msg").hide();
    } else {
        $("#msg_layer .success-msg").hide();
        $("#msg_layer .err-msg").show();
    }
}
var wxPubOriginId = "";