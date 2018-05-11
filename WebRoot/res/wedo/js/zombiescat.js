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
                userInfo: {
                    headImg: '',
                    nickname: ''
                },
                list: {
                    ownerId: '',
                    geneticCode: null,
                    fanTotalCoin: 0.0,
                    appearanceUrl: '',
                    wPubHeadImgUrl: '',
                    experienceProgressRate: {}
                },
                logs: [],
                twoImg: '',
                petBase: '',
                wPubImg: '',
                id: '',
                svgUrl: '',
                url: '',
                imgLoad: 0,
                nowDate: new Date().getTime(),
                groupList: {
                    chatPetExperinceRankItemList:[]
                },
                groupNum: "",
                taskList: {},
                testSvg: ''
            },
            created() {
              console.log('created')
              var _self = this;
              this.queryList();
              this.queryGroup();
              // this.convertImgToBase64({width: null}, './images/zombiescat/icon3-a.svg', function (base64Img) {
              //   _self.imgLoad++;
              //   _self.testSvg = base64Img;
              // });
            },
            mounted() {
              var _self = this;
              console.log('mounted！');
              // this.convertImgToBase64({width: null}, 'http://localhost:12345/ck-kitty-image/0x06012c8cf97bead5deae237070f9587f8e7a266d/690523.svg', function (base64Img) {
              //   _self.imgLoad++;
              //   _self.testSvg = base64Img;
              // });
              // this.canvasSvg();
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
                                // if (_this.imgLoad == 3) {
                                    _this.share = true;
                                    _this.canvasImg();
                                    _this.canvasSign = false;
                                    clearInterval(timer)
                                // }
                            }, 300)
                        } else {
                            _this.share = true;
                        }
                    }
                },
                queryList() {
                    var _self = this;
                    var xhr = new XMLHttpRequest();
                    // xhr.open('post', '/api/chat-pet/pet/info', true);
                    xhr.open('get', '/api/chat-pet/pet/login?id=keendo.43', true);
                    xhr.setRequestHeader('Content-type', 'application/json;charset=UTF-8');
                    xhr.send(null);
                    xhr.onreadystatechange = function () {
                        if (xhr.readyState == 4 && xhr.status == 200) {
                            var resp = JSON.parse(xhr.response);
                            if (resp.retCode == 0) {
                                _self.convertImgToBase64({width: null}, resp.result.ownerInfo.headImg, function (base64Img) {
                                    _self.imgLoad++;
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
                                _self.convertImgToBase64({
                                    width: 600,
                                    height: 600
                                }, resp.result.appearanceUrl, function (base64Img) {
                                    _self.imgLoad++;
                                    _self.petBase = base64Img;
                                });
                                _self.convertImgToBase64({width: null}, resp.result.wPubHeadImgUrl, function (base64Img) {
                                    _self.imgLoad++;
                                    _self.wPubImg = base64Img;
                                });
                                _self.logs = resp.result.petLogs;
                                _self.taskList = resp.result.todayMissions;
                                _self.twoImg = 'data:image/png;base64,' + resp.result.invitationQrCode;
                                _self.list = resp.result;
                            } else {
                                alert(resp.retMsg)
                            }
                        } else {
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
                        } else {
                        }
                    }
                },
                sureReward($event, id) {
                    var _self = this;
                    var xhr = new XMLHttpRequest();
                    var data = JSON.stringify({"itemId": id});
                    xhr.open('post', '/api/chat-pet/pet/mission/reward', true);
                    xhr.setRequestHeader('Content-type', 'application/json;charset=UTF-8');
                    xhr.send(data);
                    xhr.onreadystatechange = function () {
                        if (xhr.readyState == 4 && xhr.status == 200) {
                            var resp = JSON.parse(xhr.response);
                            if (resp.retCode == 0) {
                                var i = $event.path[1];
                                i.style.width = '150px';
                                i.querySelector('i').className = "active";
                                setTimeout(function () {
                                    _self.list = resp.result;
                                    _self.logs = resp.result.petLogs;
                                    _self.taskList = resp.result.todayMissions;
                                }, 500);
                            } else {
                                alert(resp.retMsg)
                            }
                        } else {
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
                  var canvas = document.getElementById('canvasaaa');
                  var ctx = canvas.getContext('2d');
                  var DOMURL = window.URL || window.webkitURL || window;
                  var img = new Image();
                  img.setAttribute("crossOrigin","anonymous");
                  // var img = document.querySelectorAll('zombiescat-img img');
                  var data = '<svg id="图层_1" data-name="图层 1" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 500 500"><title>画板 1 副本 7</title><path d="M441.85,287.72c-13-54.3-46.84-92.93-57.6-104.14-2-9.71-7.54-36-12.66-57.5-7-29.5-15.55-51.83-25.33-66.37C333.87,41.28,320.78,37.42,312,37.42c-17.55,0-30.09,14.52-35.5,27-2.17,5-3.84,6.76-4.45,7.3-2.86-.1-10.13-.32-18.08-.32l-3.95,0-3.95,0c-8,0-15.22.22-18.08.32-.61-.54-2.28-2.33-4.45-7.3-5.4-12.44-18-27-35.49-27-8.8,0-21.9,3.86-34.29,22.28-9.78,14.55-18.3,36.88-25.33,66.38-5.1,21.43-10.64,47.78-12.66,57.5C105,194.79,71.12,233.43,58.15,287.72c-1,4-7.56,31.47,4.86,52.54,6.08,10.32,15.84,17.41,28.23,20.48a37.09,37.09,0,0,0,8.82,1,41.49,41.49,0,0,0,4.64-.26c.62,20.56,2,38.57,2.78,48-5.7,5.52-12.58,15-10.11,27.05,1.44,7,6.29,16.47,21.2,21.73,8.21,2.89,18.75,4.29,32.22,4.29a291.37,291.37,0,0,0,32.93-2.15l.66-.1c14.94-2.52,28.11-5,38.07-10.16,12.74-6.58,19.48-17.13,20-31.36,0-.07,0-.13,0-.19,0-1.5,0-2.87,0-4.12,2.43.16,4.84.26,7.22.28h.5c2.38,0,4.79-.12,7.22-.28,0,1.25,0,2.62,0,4.12v.19c.56,14.23,7.31,24.78,20,31.36,10,5.14,23.13,7.64,38.07,10.16l.66.1a291.37,291.37,0,0,0,32.93,2.15c13.47,0,24-1.4,32.22-4.29,14.91-5.26,19.76-14.71,21.2-21.72,2.47-12.07-4.41-21.54-10.11-27.06.81-9.43,2.16-27.44,2.78-48a41.49,41.49,0,0,0,4.64.26,37.09,37.09,0,0,0,8.82-1c12.39-3.07,22.15-10.16,28.23-20.48C449.42,319.19,442.88,291.68,441.85,287.72Z"/></svg>'
                  var svg = new Blob([data], {type: 'image/svg+xml;charset=utf-8'});
                  var url = DOMURL.createObjectURL(svg);
                  img.onload = function () {
                    ctx.drawImage(img, 0, 0);
                    DOMURL.revokeObjectURL(url);
                    var canData = ctx.getImageData(0, 0, canvas.width, canvas.height);
                    console.log(canData)
                    for (var i=0; i<canData.data.length; i+=4 ) {
                      canData.data[i] = 235;
                      canData.data[i+1] = 5;
                      canData.data[i+2] = 165;
                    }
                    ctx.putImageData(canData,0,0);
                  }
                  img.src ='http://localhost:12345/res/wedo/images/zombiescat/icon2-8.svg';
                  var img2 = new Image();
                  img2.onload = function () {
                    ctx.drawImage(img2, 0, -100);
                  }
                  // img2.src ='http://localhost:12345/ck-kitty-image/0x06012c8cf97bead5deae237070f9587f8e7a266d/690523.svg';
                  img2.src = this.testSvg;
                  
                  
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
                                canvas.height = img.height;
                                canvas.width = img.width;
                            } else {
                                canvas.height = img.height;
                                canvas.width = img.width;
                            }
                        } else {
                            canvas.height = img.height;
                            canvas.width = img.width;
                        }
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