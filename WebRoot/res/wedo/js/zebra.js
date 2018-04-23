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
                headImg: '',
                nickname: ''
            },
            list: {
                ownerId: '',
                geneticCode: null,
                fanTotalCoin: 0.0,
                appearanceUrl: '',
                wPubHeadImgUrl: ''
            },
            logs: [],
            twoImg: '',
            id: '',
            svgUrl: '',
            url: ''
        },
        created() {
            var i = location.search.indexOf('=')
            this.id = location.search.slice(i + 1)
            this.queryList()
            // this.getData()
        },
        methods: {
            shareShow($event) {
                var _this = this
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
                var data = JSON.stringify({"id": this.id})
                xhr.open('post', '/api/chat-pet/pet/info', true);
                xhr.setRequestHeader('Content-type', 'application/json;charset=UTF-8')
                xhr.send(data)
                xhr.onreadystatechange = function () {
                    if (xhr.readyState == 4 && xhr.status == 200) {
                        var resp = JSON.parse(xhr.response)
                        if (resp.retCode == 0) {
                            _self.convertImgToBase64(resp.result.ownerInfo.headImg, function (base64Img) {
                                _self.userInfo.headImg = base64Img
                            });
                            _self.userInfo.nickname = resp.result.ownerInfo.nickname
                            _self.list = resp.result
                            //替换链接
                            var idx = _self.list.appearanceUrl.indexOf('googleapis.com');
                            _self.list.appearanceUrl = 'https://test.keendo.com.cn' + _self.list.appearanceUrl.slice(idx + 14);
                            //end 替换
                            _self.convertImgToBase64(resp.result.wPubHeadImgUrl, function (base64Img) {
                                _self.list.wPubHeadImgUrl = base64Img
                            });
                            _self.convertSvgToBase64(resp.result.appearanceUrl, function (base64Img) {
                                _self.svgUrl = base64Img
                            });
                            _self.logs = resp.result.petLogs
                            _self.twoImg = 'data:image/png;base64,' + resp.result.invitationQrCode
                        } else {
                            alert(resp.retMsg)
                        }
                    } else {
                    }
                }
            },
            getData() {
                var _this = this
                console.log(_this.list)
                var xhr = new XMLHttpRequest();
                xhr.open('get', _this.list.appearanceUrl, true);
                xhr.setRequestHeader('Content-type', 'application/json;charset=UTF-8')
                xhr.send()
                xhr.onreadystatechange = function () {
                    if (xhr.readyState == 4 && xhr.status == 200) {
                        _this.testImg = xhr.response
                    } else {
                    }
                }
            },
            canvasImg() {
                var _this = this
                html2canvas(document.getElementById('canvas_img')).then(function (canvas) {
                    _this.url = canvas.toDataURL();
                    document.getElementById('canvas_img').style.display = 'none'
                    // var img = new Image();
                    // img.src = canvas.toDataURL()
                    // document.getElementById('share').replaceChild(img, document.getElementById('canvas_img'));
                    // document.getElementById('share').appendChild(img);
                });
            },
            convertImgToBase64(url, callback, outputFormat) {
                var canvas = document.createElement('CANVAS'),
                    ctx = canvas.getContext('2d'),
                    img = new Image();
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
            },
            convertSvgToBase64(url, callback, outputFormat) {
                var canvas = document.createElement('CANVAS'),
                    ctx = canvas.getContext('2d'),
                    img = new Image();
                img.crossOrigin = 'Anonymous';
                img.onload = function () {
                    canvas.height = '600';
                    canvas.width = '600';
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
                    return ''
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
//     document.body.innerHTML='<p>只能微信浏览器打开</p>'
//     document.body.style.background = 'none'
// }
// if (typeof WeixinJSBridge !== "undefined") {
//     window.onload = function () {
//         alert(2)
//     }
// } else {
//     document.body.innerHTML = '<p>只能微信浏览器打开</p>'
//     document.body.style.background = 'none'
// }