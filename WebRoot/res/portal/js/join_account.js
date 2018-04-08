var Join = new Vue({
    el: "#join_account",
    data: {
        robotName: "",
        active: "active",
        gender: 0,
        character:['1']
    },
    created: function () {
        $("aside .active").removeClass("active");
        $("#join").addClass("active");
    },
    methods: {
        requireUrl: function () {
            var _self=this;
            var data={};
            if(this.robotName.trim()==""){
               dialog("带 * 号必选!",2);
                $("input[type='text']").focus();
                return;
            }else{
                data.nickname=_self.robotName;
            }
            data.gender=Number(_self.gender);
            if(this.character.length==0){
                dialog("带 * 号必选!",2);
                return;
            }else{
                $.each(_self.character,function (i,n) {
                    _self.character[i]=Number(n);
                });
                data.characterList=_self.character;
            }
            var popup = window.open("","_blank");
            $mks.syncJsonPost({
                data:data,
                url:apibase + '/wx-pub/join-url',
                success_func: function(resp){
                    if ( resp.retCode == RetCode.SUCCESS){
                        popup.location.href = resp.result;
                        // 模拟操作成功情况
                        // popup.location = 'http://localhost:8080/test.html';
                        // setTimeout(function() {
                        //     popup.document.getElementsByTagName("body")[0].innerHTML = "授权成功";
                        // }, 2000)

                        var jump = setInterval(function() {
                            if( popup.document.getElementsByTagName("body")[0].innerHTML === "授权成功" ) {
                                //自动关闭子窗口、清除Interval
                                popup.close();
                                clearInterval(jump);
                                dialog("授权成功!",1);
                                // 触发点击动作，切换到管理公众号
                                simulation(document.getElementById('manage-account'), "click", onclick)
                            }
                            //处理子窗口意外关闭情况
                            if(popup.closed) {
                                clearInterval(jump);
                            }
                        }, 1000)

                        // 点击动作回调
                        function onclick(e) {
                            e = e || window.event;
                        }
                    } else {
                        dialog(resp.retMsg,2);
                    }
                }
            })

            function simulation(obj,type,response) {
                var isIE = navigator.userAgent.match(/MSIE (\d)/i);
                isIE = isIE ? isIE[1] : undefined;            
                //给obj绑定事件
                //传统浏览器使用attachEvent 现代浏览器使用addEventListner
                isIE < 9 ? obj.attachEvent("on" + type, response):
                obj.addEventListener(type, response, false);
                // 触发自定义事件
                if( isIE < 9 ) {
                //传统浏览器
                    var event = document.createEventObject();//创建对象           
                    event.msg = "我是fireEvent触发的";//给事件对象添加属性              
                    obj.fireEvent("on" + type, event);//触发事件
                } else {
                    //现代浏览器
                    var e = document.createEvent("MouseEvents");//创建事件对象              
                    e.initEvent(type,false,false);//初始化事件对象initMouseEvent需要更多参数
                    e.msg = "我是despatchEvent触发的"; //给事件对象添加属性
                    obj.dispatchEvent(e);//触发事件      
                }      
            }
        }
    }
})