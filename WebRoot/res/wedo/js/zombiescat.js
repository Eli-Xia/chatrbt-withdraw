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
                testSvg: ''
            },
            created() {
              var _self = this;
              this.queryList();
              this.queryGroup();
              this.convertImgToBase64({width: null}, './cat.svg', function (base64Img) {
                _self.petBase = base64Img;
              });
            },
            mounted() {
              var _self = this;
              console.log('mounted！');
              // this.convertImgToBase64({width: null}, './images/zombiescat/icon3-a.svg', function (base64Img) {
              //   _self.testSvg = base64Img;
              // });
              this.canvasSvg();
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
                    var xhr = new XMLHttpRequest();
                    xhr.open('post', '/api/chat-pet/pet/info', true);
                    // xhr.open('get', '/api/chat-pet/pet/login?id=keendo.43', true);
                    xhr.setRequestHeader('Content-type', 'application/json;charset=UTF-8');
                    xhr.send(null);
                    xhr.onreadystatechange = function () {
                        if (xhr.readyState == 4 && xhr.status == 200) {
                            var resp = JSON.parse(xhr.response);
                            if (resp.retCode == 0) {
                                _self.convertImgToBase64({ width: null }, resp.result.ownerInfo.headImg+'?1', function (base64Img) {
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
                                // _self.convertImgToBase64({
                                //     width: 600,
                                //     height: 600
                                // }, resp.result.appearanceUrl, function (base64Img) {
                                //     _self.petBase = base64Img;
                                // });
                                _self.convertImgToBase64({width: null}, resp.result.wPubHeadImgUrl, function (base64Img) {
                                    _self.wPubImg = base64Img;
                                });
                                _self.logs = resp.result.petLogs;
                                _self.taskList = resp.result.todayMissions;
                                _self.invitationQrCode = 'data:image/png;base64,' + resp.result.invitationQrCode;
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
                    var data = JSON.stringify({ "pageSize": 5 });
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
                                var i = $event.target.parentNode;
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
                  var canvas = document.createElement('CANVAS'),
                  // var canvas = document.getElementById("canvasaaa"),
                      ctx = canvas.getContext('2d'),
                      img = new Image();
                  // var DOMURL = window.URL || window.webkitURL || window;
                  img.setAttribute("crossOrigin","anonymous");
                  // img.src ='./images/zombiescat/icon2-8.svg';
                  // img.onload = function () {
                  //   canvas.height = img.height;
                  //   canvas.width = img.width;
                  //   ctx.drawImage(img, 0, 0);
                  //   // var canData = ctx.getImageData(0, 0, canvas.width, canvas.height);
                  //   // for (var i=0; i<canData.data.length; i+=4 ) {
                  //   //   canData.data[i] = 235;
                  //   //   canData.data[i+1] = 5;
                  //   //   canData.data[i+2] = 165;
                  //   // }
                  //   // ctx.putImageData(canData,0,0);
                  // }

                  // draw(ctx, './images/zombiescat/icon2-8.svg');
                  // draw(ctx, './images/zombiescat/icon3-a.svg');
                  // setTimeout(function() {
                  //   console.log('overRide')
                  //   var finalImg = new Image();
                  //   finalImg.src = canvas.toDataURL();
                  //   document.getElementById('zebra-msg').replaceChild(finalImg, document.getElementById('zombiescat-img'));
                  // }, 100)
                  function draw(img, src) {
                    console.log(src)
                    img = new Image();
                    img.setAttribute("crossOrigin","anonymous");
                    img.src =src;
                    img.onload = function () {
                      canvas.height = img.height;
                      canvas.width = img.width;
                      ctx.drawImage(img, 0, 0);
                    }
                  }
                  // var img2 = new Image();
                  // img2.src ='./images/zombiescat/icon3-a.svg';
                  // img2.onload = function () {
                  //   ctx.drawImage(img2, 0, 0);
                  //   var finalImg = new Image();
                  //   finalImg.src = canvas.toDataURL();
                  //   document.getElementById('zebra-msg').replaceChild(finalImg, document.getElementById('zombiescat-img'));
                  // }
                  
                  // img2.src = this.testSvg;
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