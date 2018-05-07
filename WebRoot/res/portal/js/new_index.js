window.onload = function () {
    // canvas
    var canvas = document.getElementById('canvas'),
        container = document.getElementById('container'),
        ctx = canvas.getContext('2d'),
        w = window.innerWidth,
        h = canvas.height = window.innerHeight,
        stars = [],
        count = 0,
        maxStars = 300;
    if (w < 1000) {
        w = 1000;
    }
    canvas.width = w;
    container.style.width = w + 'px';
    container.style.height = h + 'px';
    $(window).resize(function () {
        w = window.innerWidth;
        h = canvas.height = window.innerHeight;
        if (w < 1000) {
            w = 1000;
        }
        canvas.width = w;
        container.style.width = w + 'px';
        container.style.height = h + 'px';
        stars = [];
        count = 0;
        for (var i = 0; i < maxStars; i++) {
            new Star();
        }
    });
    var canvas2 = document.createElement('canvas'),
        ctx2 = canvas2.getContext('2d');
    canvas2.width = 100;
    canvas2.height = 100;
    var half = canvas2.width / 2;
    ctx2.fillStyle = '#fff';
    ctx2.beginPath();
    ctx2.arc(half, half, half, 0, Math.PI * 2);
    ctx2.fill();

    function random(min, max) {
        if (arguments.length < 2) {
            max = min;
            min = 0;
        }
        if (min > max) {
            var hold = max;
            max = min;
            min = hold;
        }
        return Math.floor(Math.random() * (max - min + 1)) + min;
    }

    var Star = function () {
        this.orbitRadius = random(3);
        this.positionX = random(10, w);
        this.positionY = random(100, h);
        this.transformY = random(2, 5) / 10;
        this.alpha = random(2, 10) / 10;
        count++;
        stars[count] = this;
    }
    Star.prototype.draw = function () {
        if (this.positionY < 0) {
            this.positionX = random(10, w);
            this.positionY = random(h - 10, h);
        }
        ctx.globalAlpha = this.alpha;
        ctx.drawImage(canvas2, this.positionX, this.positionY -= this.transformY, this.orbitRadius, this.orbitRadius);
    }
    for (var i = 0; i < maxStars; i++) {
        new Star();
    }

    function animation() {
        ctx.globalCompositeOperation = 'source-over';
        ctx.fillStyle = '#000';
        ctx.fillRect(0, 0, w, h);
        ctx.globalCompositeOperation = 'lighter';
        for (var i = 1, l = stars.length; i < l; i++) {
            stars[i].draw();
        }
        window.requestAnimationFrame(animation);
    }

    animation();
    var joinBox = document.querySelector('.join-box'),
        Login = document.querySelector('#login'),
        Register = document.querySelector('#register'),
        earth = document.querySelector('.earth'),
        future = document.querySelector('.future'),
        Reset = document.querySelector('body>.logo'),
        btns = document.querySelectorAll('.login button');
    // 滚动
    var divTarget = document.querySelectorAll('#container>div'),
        liTarget = document.querySelectorAll('#pages>li'),
        targetSign = 0;

    function scrollTarget() {
        var index = Math.floor(targetSign / 5);
        for (var i = 0; i < divTarget.length; i++) {
            liTarget[i].className = '';
            if (i < index) {
                divTarget[i].style.left = "-100%"
            } else if (i > index) {
                divTarget[i].style.left = "100%"
            } else {
                divTarget[i].style.left = '0';
                liTarget[i].className = 'active'
            }
        }
    }

    function loginModel() {
        targetSign = 0;
        scrollTarget();
        joinBox.className = 'join-box login-sign';
        Register.style.top = '500px';
        Login.style.top = '70px';
        earth.style.bottom = '-100px';
        future.style.opacity = 0
    }

    function registerModel() {
        targetSign = 0;
        scrollTarget();
        joinBox.className = 'join-box register-sign';
        Login.style.top = '600px';
        Register.style.top = '70px';
        earth.style.bottom = '-100px';
        future.style.opacity = 0
    }

    Reset.onclick = function () {
        targetSign = 0;
        scrollTarget();
        joinBox.className = 'join-box';
        Login.style.top = '600px';
        Register.style.top = '500px';
        earth.style.bottom = '0';
        future.style.opacity = 1
    };
    btns[1].addEventListener('click', loginModel);
    btns[0].addEventListener('click', registerModel);
    liTarget[0].addEventListener('click', function () {
        targetSign = 0;
        scrollTarget()
    });
    liTarget[1].addEventListener('click', function () {
        targetSign = 9;
        scrollTarget()
    });
    var scrollFunc = function (e) {
        e = e || window.event;
        if (e.wheelDelta) {
            if (e.wheelDelta > 0) {
                if (targetSign > 0) {
                    targetSign--;
                    if (targetSign % 5 == 0) {
                        scrollTarget()
                    }
                }
            }
            if (e.wheelDelta < 0) {
                if (targetSign < divTarget.length * 5 - 1) {
                    targetSign++;
                    if (targetSign % 5 == 0) {
                        scrollTarget()
                    }
                }
            }
        } else if (e.detail) {
            if (e.detail > 0) {
                if (targetSign > 0) {
                    targetSign--;
                    if (targetSign % 5 == 0) {
                        scrollTarget()
                    }
                }
            }
            if (e.detail < 0) {
                if (targetSign < divTarget.length * 5 - 1) {
                    targetSign++;
                    if (targetSign % 5 == 0) {
                        scrollTarget()
                    }
                }
            }
        }
    };
    new Vue({
        el: '#login',
        data: {
            email: '',
            password: '',
            remember: false,
            active: "remember-sure",
            errShow: false
        },
        created: function () {
            var _self = this;
            if (localStorage.getItem("remember")) {
                var holdTime = localStorage.getItem("holdTime");
                var nowTime = new Date().getTime();
                var saveTime = nowTime - holdTime;
                if (saveTime < 86400000 * 3) {
                    _self.password = localStorage.getItem("password");
                    _self.email = localStorage.getItem("email");
                    _self.remember = localStorage.getItem("remember");
                } else {
                    localStorage.removeItem("email");
                    localStorage.removeItem("password");
                    localStorage.removeItem("remember");
                    localStorage.removeItem("holdTime");
                }
            }
            ;
        },
        methods: {
            loginClick: function () {
                var data = {};
                var _self = this;
                if ($.trim(this.email) == '') {
                    alert('用户名不能为空');
                    $("#login [type='text']").focus();
                    return;
                } else if ($.trim(this.password) == '') {
                    alert('密码不能为空');
                    $("#login [type='password']").focus();
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
                                if (!localStorage.getItem("remember")) {
                                    var nowTime = new Date().getTime();
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
                            _self.errShow = true
                        }
                    }
                })
            },
            registerClick: function () {
                registerModel();
                this.errShow = false
            }
        }
    });
    new Vue({
        el: '#register',
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
                if (this.regNick.test(this.nickname)) {
                    data.nickname = this.nickname;
                } else {
                    $("#nickname").focus();
                    return;
                }
                if (this.regEmail.test(this.email)) {
                    data.email = this.email;
                } else {
                    $("#user_email").focus();
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
            },
            loginClick: function () {
                loginModel();
            }
        }
    });
    if (document.addEventListener) {//firefox
        document.addEventListener('DOMMouseScroll', scrollFunc, false);
    }
    //滚动滑轮触发scrollFunc方法  //ie 谷歌
    window.onmousewheel = document.onmousewheel = scrollFunc;

}