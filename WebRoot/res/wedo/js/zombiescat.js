var ua = navigator.userAgent.toLowerCase();
var isWeixin = ua.indexOf('micromessenger') != -1 || ua.indexOf('webbrowser') != -1;
// if (isWeixin) {
window.onload = function () {
    var vm = new Vue({
        el: '#app',
        data: {
            share: false,
            userCodeShow: false,
            bgColor: '1b0035',
            canvasSign: true,
            ruleShow: true,
            logShow: true,
            userInfo: {
                headImg: '',
                nickname: ''
            },
            list: {
                ownerId: '',
                geneticCode: null,
                fanTotalCoin: 0.0,
                appearanceUrl: '',
                appearance: [],
                wPubHeadImgUrl: '',
                experienceProgressRate: {},
                goldItems: []
            },
            logs: [],
            colorList: [],
            textureColor: {},
            invitationQrCode: '',
            petBase: '',
            wPubImg: '',
            id: '',
            svgUrl: '',
            url: '',
            nowDate: new Date().getTime(),
            groupList: {
                chatPetExperinceRankItemList: []
            },
            groupNum: '',
            taskList: {},
            testSvg: '',
            stop: false
        },
        created() {
            var _self = this;
            this.list.appearance = {
                chatPetType: 1,
                object: {
                    eye: '0',
                    infill: '0',
                    mouth: '0',
                    outline: '0',
                    texture: '0a',
                    textureColor: '0',
                    textureShadow: '0'
                }
            }
            const sign = localStorage.getItem('ruleShow')
            if (sign !== null) {
                sign === 'true' ? this.ruleShow = true : this.ruleShow = false
            }
            const logShow = localStorage.getItem('logShow')
            if (logShow !== null) {
                logShow === 'true' ? this.logShow = true : this.logShow = false
            }
            this.queryList();
            this.queryAllColor();
            this.queryGroup();
        },
        methods: {
            shareShow($event) {
                var _this = this;
                if ($event.target.id == 'share') {
                    this.share = false;
                }
                if ($event.target.className == 'share-target') {
                    if (this.canvasSign) {
                        var timer = setInterval(function () {
                            _this.share = true;
                            _this.canvasImg();
                            _this.canvasSign = false;
                            clearInterval(timer)
                        }, 300)
                    } else {
                        _this.share = true;
                    }
                }
            },
            queryList() {
                var _self = this;
                // 获取到数据前先隐藏形象
                var zombiescat = document.querySelector('.zombiescat-img');
                zombiescat.style.display = 'none';
                var xhr = new XMLHttpRequest();
                xhr.open('post', '/api/chat-pet/pet/info', true);
                // xhr.open('get', '/api/chat-pet/pet/login?id=keendo.43', true);
                xhr.setRequestHeader('Content-type', 'application/json;charset=UTF-8');
                xhr.send(null);
                xhr.onreadystatechange = function () {
                    if (xhr.readyState == 4 && xhr.status == 200) {
                        var resp = JSON.parse(xhr.response);
                        if (resp.retCode == 0) {
                            _self.convertImgToBase64({width: null}, resp.result.ownerInfo.headImg + '?1', function (base64Img) {
                                _self.userInfo.headImg = base64Img;
                            });
                            //替换链接
                            var idx = resp.result.appearanceUrl.indexOf('googleapis.com');
                            if (location.hostname == 'localhost') {
                                resp.result.appearanceUrl = 'http://localhost:12345' + resp.result.appearanceUrl.slice(idx + 14);
                            } else {
                                resp.result.appearanceUrl = location.origin + resp.result.appearanceUrl.slice(idx + 14);
                            }
                            //end 替换
                            _self.userInfo.nickname = resp.result.ownerInfo.nickname;
                            _self.convertImgToBase64({width: null}, resp.result.wPubHeadImgUrl, function (base64Img) {
                                _self.wPubImg = base64Img;
                            });
                            _self.logs = resp.result.petLogs;
                            _self.taskList = resp.result.todayMissions;
                            _self.invitationQrCode = 'data:image/png;base64,' + resp.result.invitationQrCode;
                            _self.list = resp.result;
                            setTimeout(function () {
                                _self.randomLocation()
                            }, 300);
                            // 获取数据后重新显示形象
                            document.querySelector('.zombiescat-img').style.display = 'block';
                            var colorIdx = _self.list.appearance.object.textureColor;
                            var textureColor = `icon3-${colorIdx}`
                            // 改变纹理颜色
                            setTimeout(function () {
                                var keys = Object.keys(_self.colorList).map(function (c) {
                                    if (_self.colorList[c].key === colorIdx) {
                                        _self.textureColor = {
                                            color: `#${_self.colorList[c].rgbValue}`
                                        }
                                        console.log(_self.textureColor)
                                    }
                                })
                            }, 100);
                            _self.canvasSvg();
                        } else {
                            alert(resp.retMsg)
                        }
                    }
                }
            },
            queryGroup() {
                var _self = this;
                var xhr = new XMLHttpRequest();
                var data = JSON.stringify({"pageSize": 5});
                xhr.open('post', '/api/chat-pet/ethnic-groups/rank', true);
                xhr.setRequestHeader('Content-type', 'application/json;charset=UTF-8');
                xhr.send(data);
                xhr.onreadystatechange = function () {
                    if (xhr.readyState == 4 && xhr.status == 200) {
                        var resp = JSON.parse(xhr.response);
                        if (resp.retCode == 0) {
                            _self.groupList = resp.result;
                        } else {
                            alert(resp.retMsg)
                        }
                    }
                }
            },
            queryAllColor() {
                var _this = this;
                var xhr = new XMLHttpRequest();
                xhr.open('post', '/api/chat-pet/color/all', true);
                xhr.setRequestHeader('Content-type', 'application/json;charset=UTF-8');
                xhr.send(null);
                xhr.onreadystatechange = function () {
                    if (xhr.readyState == 4 && xhr.status == 200) {
                        var resp = JSON.parse(xhr.response);
                        if (resp.retCode == 0) {
                            _this.colorList = resp.result;
                        } else {
                            alert(resp.retMsg)
                        }
                    }
                }
            },
            ruleShowToggle() {
                this.ruleShow = !this.ruleShow
                localStorage.setItem('ruleShow', this.ruleShow)
            },
            logShowToggle() {
                this.logShow = !this.logShow
                localStorage.setItem('logShow', this.logShow)
            },
            randomLocation() {
                var tokens = document.querySelectorAll('.token-reward');
                var token = document.querySelector('.token-reward');
                var zebraBg = document.querySelector('.zebra-bg');

                function randomTop() {
                    return Math.floor(Math.random() * (zebraBg.clientHeight - (2 * token.clientHeight)) + token.clientHeight) + 'px'
                }

                var diff = (zebraBg.clientWidth - (zebraBg.clientWidth * 0.6)) / 2;

                function randomLeft() {
                    if (Math.random() > 0.5) {
                        return Math.floor((Math.random() * (diff - (2 * token.clientWidth))) + token.clientWidth) + 'px';
                    } else {
                        return Math.floor(Math.random() * (zebraBg.clientWidth - (zebraBg.clientWidth - diff)) + (zebraBg.clientWidth - diff - token.clientWidth)) + 'px'
                    }
                }

                tokens.forEach(function (i) {
                    i.style.top = randomTop();
                    i.style.left = randomLeft();
                })
            },
            sureReward($event, rewardItemId, missionItemId) {
                $event.preventDefault();
                var _self = this;
                var e = $event.target;
                if (e.nodeName == 'IMG') {
                    e = e.parentNode;
                }
                if (e.getAttribute('data-sign')) {
                    return;
                } else {
                    e.setAttribute('data-sign', true)
                }
                var xhr = new XMLHttpRequest();
                var data = {
                    rewardItemId: rewardItemId,
                    missionItemId: missionItemId
                };
                data = JSON.stringify(data);
                xhr.open('post', '/api/chat-pet/pet/mission/reward', true);
                xhr.setRequestHeader('Content-type', 'application/json;charset=UTF-8');
                xhr.send(data);
                xhr.onreadystatechange = function () {
                    if (xhr.readyState == 4) {
                        if (xhr.status == 200) {
                            var resp = JSON.parse(xhr.response);
                            if (resp.retCode == 0) {
                                e.className = 'token-reward tran';
                                setTimeout(function () {
                                    e.className = 'token-reward hide';
                                    e.setAttribute('data-sign', '');
                                    _self.list.goldItems = resp.result.goldItems;
                                    _self.logs = resp.result.petLogs;
                                    _self.taskList = resp.result.todayMissions;
                                    _self.groupList = resp.result.groupRank;
                                    _self.list.fanTotalCoin = resp.result.fanTotalCoin;
                                    _self.list.goldItems = resp.result.goldItems;
                                    _self.list.chatPetLevel = resp.result.chatPetLevel;
                                    _self.list.experienceProgressRate = resp.result.experienceProgressRate;
                                }, 1000);
                            } else {
                                e.setAttribute('data-sign', '');
                                alert(resp.retMsg);
                            }
                        } else {
                            e.setAttribute('data-sign', '');
                            alert("网络出错,请重试!");
                        }
                    }
                }
            },
            canvasImg() {
                html2canvas(document.getElementById('canvas_img'), {
                    useCORS: true,
                    allowTaint: true,
                    taintTest: false,
                }).then(function (canvas) {
                    var img = new Image();
                    img.src = canvas.toDataURL();
                    document.getElementById('share').replaceChild(img, document.getElementById('canvas_img'));
                    // document.getElementById('share').appendChild(img);
                });
            },
            canvasSvg() {
                var zombiescat = this.list.appearance.object;
                var canvas = document.createElement('CANVAS'),
                    ctx = canvas.getContext('2d'),
                    loads = 0;
                var originImg = new Image();
                originImg.src = './images/zombiescat/infill/0.svg';
                canvas.height = originImg.height;
                canvas.width = originImg.width;
                var sourceArr = [`images/zombiescat/outline/${zombiescat.outline}.svg`, `images/zombiescat/infill/${zombiescat.infill}.svg`,
                    `images/zombiescat/texture/${zombiescat.texture}.svg#图层_1`, `images/zombiescat/textureShadow/${zombiescat.textureShadow}.svg`, `images/zombiescat/eye/${zombiescat.eye}.svg`, `images/zombiescat/mouth/${zombiescat.mouth}.svg`];
                var len = sourceArr.length;

                function draw() {
                    var newImg = new Image();
                    newImg.setAttribute('crossOrigin', 'anonymous');
                    newImg.src = sourceArr[loads];
                    newImg.onload = function () {
                        ctx.drawImage(newImg, 0, 0);
                        loads++
                        if (loads == len) {
                            var finalImg = new Image();
                            finalImg.src = canvas.toDataURL();
                            document.getElementById('zebra-msg').replaceChild(finalImg, document.getElementById('zombiescat-img'));
                        } else {
                            draw()
                        }
                    }
                }

                draw()
            },

            convertImgToBase64(param, url, callback, outputFormat) {
                var canvas = document.createElement('CANVAS'),
                    ctx = canvas.getContext('2d'),
                    img = new Image();
                img.crossOrigin = 'Anonymous';
                img.onload = function () {
                    if (param.width) {
                        if (ua.indexOf('android') != -1) {
                            img.height = param.height;
                            img.width = param.width;
                        }
                    }
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
//     document.body.innerHTML = '<p>只能微信浏览器打开</p>';
//     document.body.style.background = 'none'
// }