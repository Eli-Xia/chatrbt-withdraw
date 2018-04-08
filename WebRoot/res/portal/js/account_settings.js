var Menage = new Vue({
    el: "#account_settings",
    data: {
        nickname: '',
        email: '',
        userTel: '',
        imgUrl: '',
        defaultUrl: base + "/res/portal/images/default.png",
        show: false,
        regEmail: /^([a-zA-Z0-9._-])+@([a-zA-Z0-9_-])+(.[a-zA-Z0-9_-])+/,//邮箱格式
        disable: true,
        active: "active",
        dis: "dis",
        alterActive: false,
    },
    watch: {
        nickname: function (newval, oldval) {
            if (oldval != '') {
                this.alterActive = true;
            }
        },
        email: function (newval, oldval) {
            if (oldval != '') {
                this.alterActive = true;
            }
        },
        userTel: function (newval, oldval) {
            if (oldval != '') {
                this.alterActive = true;
            }
        },
        imgUrl: function (newval, oldval) {
            // if(oldval!=''){
            //     this.alterActive=true;
            // }
        },
    },
    created: function () {
        var _self = this;
        this.requireContent();
        var options = {
            thumbBox: '.thumbBox',
            spinner: '.spinner',
            imgSrc: ''
        };
        $(function () {
            var cropper = $('#clip_area').cropbox(options);
            $('#file').on('change', function () {
                var reader = new FileReader();
                reader.onload = function (e) {
                    options.imgSrc = e.target.result;
                    cropper = $('#clip_area').cropbox(options);
                }
                var file = this.files[0].type;
                var i = file.indexOf("/");
                var format = file.slice(0, i);
                if (format == "image") {
                    reader.readAsDataURL(this.files[0]);
                    $("#view").show();
                } else {
                    dialog("请上传正确的图片格式!", 2);
                }
            });
            $('#clip_btn').on('click', function () {
                var img = cropper.getDataURL();
                // $("#user_img").attr("src",img);
                _self.imgUrl = img;
                $("#view").hide();
            })
        })
    },
    methods: {
        requireContent: function () {
            var _self = this;
            $mks.syncJsonPost({
                url: apibase + "/user/info/query",
                success_func: function (res) {
                    if (res.retCode == RetCode.SUCCESS) {
                        _self.nickname = res.result.nickname;
                        _self.email = res.result.contactEmail;
                        _self.userTel = res.result.contactPhone;
                        _self.alterActive=false;
                    } else {
                        dialog(res.retMsg, 2)
                    }
                }
            })
        },
        reset: function () {
            if (this.alterActive == false) {
                return;
            }
            this.nickname = '';
            this.email ='';
            this.userTel ='';
            this.requireContent();
        },
        saveUpdate: function () {
            var data = {};
            var _self = this;
            if (this.alterActive == false) {
                return;
            }
            if ($.trim(this.nickname) == "") {
                dialog("昵称不能为空!", 2);
                $("#nickname").focus();
                return;
            } else {
                data.nickname = this.nickname;
            };
            // if (this.regEmail.test(this.email)) {
            data.contactEmail = this.email;
            // }
            data.contactPhone = this.userTel;
            $mks.syncJsonPost({
                url: apibase + "/user/info/update",
                data: data,
                success_func: function (res) {
                    if (res.retCode == RetCode.SUCCESS) {
                        dialog("保存成功！", 1);
                        _self.disable = true;
                        _self.alterActive=false;
                    } else {
                        dialog(res.retMsg, 2);
                    }
                }
            })
        },
        disToggle: function () {
            this.disable = !this.disable
        }
    }
});