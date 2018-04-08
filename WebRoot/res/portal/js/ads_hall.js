new Vue({
    el: '#ads_hall',
    data: {
        showModel: 1,
        tipShow: true,
        pushList: [],
        closeList: [],
        preList: [],
        pushShow: false,
        closedShow:false,
        detail: [],
        page: 1,
        pages: 1,
        wxList: [],
        wxPushIds: [],
        wxCloseIds: [],
        index:null,
        key:null
    },
    computed: {
        maxIndex: function () {
            return this.page * 4
        },
        minIndex: function () {
            return (this.page - 1) * 4
        }
    },
    created: function () {
        var _self = this;
        $('#ads_hall').on('click', '.web-page button', function (e) {
            var text = e.target.innerHTML;
            if (Number(text)) {
                if (_self.page != Number(text)) {
                    _self.page = Number(text);
                    _self.updateList()
                }
            }
        })
        this.adsAllList();
    },
    methods: {
        adsAllList: function () {
            var _self = this;
            $mks.syncJsonPost({
                url: apibase + "/ad-hall/item/list",
                success_func: function (resp) {
                    if (resp.retCode == RetCode.SUCCESS) {
                        _self.pushList = resp.result.pushList;
                        _self.closeList = resp.result.closePushList;
                        _self.preList = resp.result.prePushList;
                        $("#ad_layer").hide()
                    } else {
                        dialog(resp.retMsg, 2)
                    }
                }
            })
        },
        toggleModel: function (key) {
            this.showModel = key;
        },
        pushDetail: function (id,index,key) {
            var _self = this;
            this.detail = [];
            this.wxList = [];
            this.page=1;
            this.index=index;
            this.key=key;
            var formData = new FormData();
            formData.append("adId", id);
            $.ajax({
                url: apibase + '/ad-hall/item/detail',
                type: 'POST',
                data: formData,
                cache: false,
                processData: false,
                contentType: false,
                success: function (resp) {
                    if (resp.retCode == RetCode.SUCCESS) {
                        _self.detail = resp.result;
                        _self.pushShow = true;
                        $('#global_layer').show();
                        if (resp.result.wxPubs.length > 0) {
                            _self.pages = Math.ceil(_self.detail.wxPubs.length / 4);
                            _self.wxList = _self.detail.wxPubs;
                            _self.eachWXList();
                            $('#ads_hall .web-page').html(SaimiriPagination.webPaging(_self.page, _self.pages));
                        }
                    } else {
                        dialog(resp.retMsg, 2);
                    }
                }
            });
        },
        closeDetail: function (id) {
            var _self = this;
            this.detail = [];
            this.wxList = [];
            this.page=1;
            var formData = new FormData();
            formData.append("adId", id);
            $.ajax({
                url: apibase + '/ad-hall/item/detail',
                type: 'POST',
                data: formData,
                cache: false,
                processData: false,
                contentType: false,
                success: function (resp) {
                    if (resp.retCode == RetCode.SUCCESS) {
                        _self.detail = resp.result;
                        _self.closedShow = true;
                        $('#global_layer').show();
                        if (resp.result.wxPubs.length > 0) {
                            _self.pages = Math.ceil(_self.detail.wxPubs.length / 4);
                            _self.wxList = _self.detail.wxPubs;
                            $('#ads_hall .web-page').html(SaimiriPagination.webPaging(_self.page, _self.pages));
                        }
                    } else {
                        dialog(resp.retMsg, 2);
                    }
                }
            });
        },
        hideDetail: function () {
            this.pushShow = false;
            this.closedShow = false;
            $('#global_layer').hide();
        },
        updateState: function (id, state, index, sign) {
            var formData = new FormData();
            var key = 0;
            if (state == 0) {
                key = 1
            }
            var _self = this;
            formData.append("adId", id);
            formData.append("state", key);
            $.ajax({
                url: apibase + '/ad-hall/state/update',
                type: 'POST',
                data: formData,
                cache: false,
                processData: false,
                contentType: false,
                success: function (resp) {
                    if (resp.retCode == RetCode.SUCCESS) {
                        if (sign == 2) {
                            _self.preList[index].state = key
                        } else {
                            _self.pushList[index].state = key
                        }
                    } else {
                        dialog(resp.retMsg, 2);
                    }
                }
            });
        },
        lastWebPage: function () {
            if (this.page - 1 > 0) {
                this.page -= 1;
                this.updateList()
            }
        },
        nextWebPage: function () {
            if (this.page + 1 <= this.pages) {
                this.page += 1;
                this.updateList()
            }
        },
        updateList: function () {
            $('#ads_hall .web-page').html(SaimiriPagination.webPaging(this.page, this.pages));
        },
        eachWXList:function () {
            var _self=this;
            _self.wxPushIds=[];
            _self.wxCloseIds=[];
            this.wxList.forEach(function (value) {
                if(value.isExclude==1){
                    _self.wxPushIds.push(value.wxPubId)
                }else{
                    _self.wxCloseIds.push(value.wxPubId)
                }
            })
        },
        alertWxPushIds:function (key) {
            var i=this.wxPushIds.indexOf(key)
            if(i==-1){
                this.wxPushIds.push(key);
                this.wxCloseIds.splice(i,1)
            }else{
                this.wxCloseIds.push(key);
                this.wxPushIds.splice(i,1)
            }
        },
        updatePushWX:function (id) {
            var _self=this;
            $mks.syncJsonPost({
                data: {
                    adId: id,
                    includeWxPubIds: this.wxPushIds,
                    excludeWxPubIds: this.wxCloseIds,
                },
                url: apibase + '/ad-hall/item/detail/confirm',
                success_func: function (resp) {
                    if (resp.retCode == RetCode.SUCCESS) {
                        dialog(resp.retMsg,1);
                        if(_self.wxPushIds.length==0){
                            if(_self.key==1){
                                _self.pushList[_self.index].state=0
                            }else{
                                _self.preList[_self.index].state=0
                            }
                        }

                    } else {
                        dialog(resp.retMsg, 2);
                    }
                    _self.pushShow=false;
                    $('#global_layer').hide()
                }
            })
        }
    },
    filters: {
        formatDateTime: function (time) {
            return $mks.formatDateTime(time);
        },
        formatAdType: function (type) {
            if (type == 1) {
                return "图文类型"
            } else if (type == 2) {
                return "文本类型"
            } else {
                return "图片类型"
            }
        },
    }
})

function svgImg() {
    var imgObj = document.querySelectorAll('.gray');

    function gray(imgObj) {
        var canvas = document.createElement('canvas');
        var canvasContext = canvas.getContext('2d');
        var imgW = 204;
        var imgH = 130;
        canvas.width = imgW;
        canvas.height = imgH;
        canvasContext.drawImage(imgObj, 0, 0);
        var imgPixels = canvasContext.getImageData(0, 0, imgW, imgH);

        for (var y = 0; y < imgPixels.height; y++) {
            for (var x = 0; x < imgPixels.width; x++) {
                var i = (y * 4) * imgPixels.width + x * 4;
                var avg = (imgPixels.data[i] + imgPixels.data[i + 1] + imgPixels.data[i + 2]) / 3;
                imgPixels.data[i] = avg;
                imgPixels.data[i + 1] = avg;
                imgPixels.data[i + 2] = avg;
            }
        }
        canvasContext.putImageData(imgPixels, 0, 0, 0, 0, imgPixels.width, imgPixels.height);
        return canvas.toDataURL();
    }

    for (var i = 0; i < imgObj.length; i++) {
        imgObj[i].src = gray(imgObj[i]);
    }
}
$(function () {
    svgImg()
})