(function () {
    var mainHeader = new Vue({
        el: '#main-header',
        data: {
            avatar: 'http://hwjres.oss-cn-shenzhen.aliyuncs.com/avatar_default.png'
        },
        created: function () {
            var _self = this;
            //获取用户信息
            $mks.syncJsonGet({
                url: apibase + '/admin/user/curuser/detail',
                success_func: function (res) {
                    if (res.retCode == RetCode.SUCCESS) {
                        _self.avatar = res.result.avatar;
                    }
                }
            });
        },
        methods: {
            logout: function () {
                $.ajax({
                    url: apibase + '/admin/user/logout',
                    type: 'GET',
                    async: false,
                    data: {},
                    success: function (res) {
                        if (res.retCode == RetCode.SUCCESS) {
                            location.href = base + '/admin/login.html';
                        }
                    }
                });
            },
            avatar: function () {
                return this.avatar;
            }
        }
    })
    var login = new Vue({
        el: '#main_sidebar',
        data: {
            navList: [],
            avatar: 'http://hwjres.oss-cn-shenzhen.aliyuncs.com/avatar_default.png'
        },
        created: function () {
            var _self = this;
            //获取导航列表
            $.get(apibase + '/admin/navi', function (res) {
                if (res.retCode == RetCode.SUCCESS) {
                    _self.navList = res.result;
                } else {
                    if (res.retCode == -1) {
                        $('.navbar-nav').css('display', 'none');
                        $('#login').css('display', 'block');
                        $('body').removeClass('sidebar-mini').addClass('sidebar-collapse');
                    } else {
                        alert(res.message);
                        if (res.retCode == RetCode.NO_LOGIN) {
                            location.href = base + '/admin/login.html';
                        }
                    }
                }
            });
            //获取用户信息
            $mks.syncJsonGet({
                url: apibase + '/admin/user/curuser/detail',
                success_func: function (res) {
                    if (res.retCode == RetCode.SUCCESS) {
                        _self.avatar = res.result.avatar;
                    }
                }
            });
        },
        methods: {
            avatar: function () {
                return this.avatar;
            }
        }
    })
}());
