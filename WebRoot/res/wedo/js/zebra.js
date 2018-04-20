// var ua = navigator.userAgent.toLowerCase();
// var isWeixin = ua.indexOf('micromessenger') != -1;
// if (isWeixin) {
window.onload = function () {
    new Vue({
        el: '#app',
        data: {
            share: false,
            userCodeShow: false,
            bgColor: '1b0035',
            canvasSign: true,
            userInfo: {
                headImg: 'images/demo.png',
                nickname: '四木'
            },
            list: {
                ownerId: 'acB7dmClbR',
                geneticCode: 15147396010,
                fanTotalCoin: 0.0
            },
            logs: [
                {
                    id: '',
                    createTime: '',
                    coin: 3,
                    content: '完成每日阅读文章任务，经验值'
                },
                {
                    id: '',
                    createTime: '',
                    coin: 3,
                    content: '完成每日阅读文章任务，经验值'
                },
                {
                    id: '',
                    createTime: '',
                    coin: 3,
                    content: '完成每日阅读文章任务，经验值'
                }
            ],
            twoImg: 'images/two.png',
            id: ''
        },
        created() {
            var i = location.search.indexOf('=')
            this.id =location.search.slice(i+1)
            console.log(this.id)
            this.queryList()
        },
        methods: {
            shareShow($event) {
                if ($event.target.className == 'share-target' || $event.target.id == 'share') {
                    this.share = !this.share
                    if ($event.target.className == 'share-target' && this.canvasSign) {
                        this.canvasImg()
                        this.canvasSign = false
                    }
                }
            },
            queryList() {
                var _self = this
                var xhr = new XMLHttpRequest();
                var data = JSON.stringify({"id": "1"})
                xhr.open('post', '/api/chat-pet/pet/info', true);
                xhr.setRequestHeader('Content-type', 'application/json;charset=UTF-8')
                xhr.send(data)
                xhr.onreadystatechange = function () {
                    if (xhr.readyState == 4 && xhr.status == 200) {
                        var resp = JSON.parse(xhr.response)
                        if (resp.retCode == 0) {
                            _self.convertImgToBase64(resp.result.ownerInfo.headImg, function(base64Img){
                                _self.userInfo.headImg = base64Img
                            });
                            _self.userInfo.nickname = resp.result.ownerInfo.nickname
                            _self.list = resp.result
                            _self.logs = resp.result.petLogs
                        } else {
                            alert(resp.retMsg)
                        }
                    } else {
                    }
                }
            },
            canvasImg() {
                html2canvas(document.getElementById('canvas_img')).then(function (canvas) {
                    var img = new Image();
                    img.src = canvas.toDataURL()
                    document.getElementById('share').replaceChild(img,document.getElementById('canvas_img'));
                    // document.getElementById('share').appendChild(img);
                });
            },
            convertImgToBase64(url, callback, outputFormat) {
                var canvas = document.createElement('CANVAS'),
                    ctx = canvas.getContext('2d'),
                    img = new Image;
                img.crossOrigin = 'Anonymous';
                img.onload = function () {
                    canvas.height = img.height;
                    canvas.width = img.width;
                    ctx.drawImage(img, 0, 0);
                    var dataURL = canvas.toDataURL(outputFormat || 'image/png');
                    callback.call(this, dataURL);
                    canvas = null;
                };
                img.src = url;
            }
        },
        filters: {
            formatDate(ms) {
                if (ms) {
                    var d = new Date(ms);
                    return d.getFullYear() + '-' + add_zero(d.getMonth() + 1) + '-'
                        + add_zero(d.getDate());
                } else {
                    return '--'
                }

                function add_zero(temp) {

                    if (temp < 10) {
                        return "0" + temp;
                    } else {
                        return temp;
                    }
                }
            },
            formatTime(ms) {
                var d = new Date(ms);
                return add_zero(d.getHours()) + ':' + add_zero(d.getMinutes()) + ':' + add_zero(d.getSeconds());

                function add_zero(temp) {
                    if (temp < 10) {
                        return "0" + temp;
                    } else {
                        return temp;
                    }
                }
            }
        }
    })
}

// } else {
// document.body.innerHTML='<p>只能微信浏览器打开</p>'
// }
// if (typeof WeixinJSBridge !== "undefined") {
//     window.onload = function () {
//         alert(2)
//     }
// } else {
//     document.body.innerHTML = '<p>只能微信浏览器打开</p>'
// }