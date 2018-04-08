var Child = {
    props: ['wxOriginid', 'wxPubname','requery'],
    template: '\
    <div class="box">\
        <div class="box-body">\
            <table id="example" class="table table-bordered table-striped">\
                <thead>\
                <tr>\
                    <th>ID</th>\
                    <th>标题</th>\
                    <th>公众号平台获得URL</th>\
                    <th>手动录入URL</th>\
                    <th>接入时间</th>\
                </tr>\
                </thead>\
                <tbody class="gw-tbody">\
                <tr v-for="item in wxNewsList">\
                    <td>{{item.id}}</td>\
                    <td style="max-width: 150px;">{{item.title}}</td>\
                    <td style="width: 30%; max-width: 300px; word-break: break-all;">{{item.url}}</td>\
                    <td class="edit-url"><textarea rows="5" type="text" @blur="addUrl($event, item.id)">{{item.url2}}</textarea></td>\
                    <td>{{item.createTime | formateTimeStamp}}</td>\
                </tr>\
                </tbody>\
            </table>\
        </div>\
        <button type="button" class="btn btn-warning right" @click="editNewsUrl">提交修改</button>\
        <nav class="deco-pagination-container">\
            <ul class="page">\
                <li>\
                    <button href="#" aria-label="Previous" class="lastPage" @click="lastPage">\
                        <span aria-hidden="true">&laquo;</span>\
                    </button>\
                </li>\
                <li id="pager"></li>\
                <li>\
                    <button href="#" aria-label="Next" class="nextPage" @click="nextPage">\
                        <span aria-hidden="true">&raquo;</span>\
                    </button>\
                </li>\
            </ul>\
        </nav>\
    </div>\
    ',
    data: function() {
        return {
            wxNewsList: [],
            wxPubName: '',
            mapObj: {},
        }
    },
    created: function() {
        var _this = this;
        $.extend(SaimiriPagination.defaults,{curPage: 1, total: 0, pageSize:10});
        this.queryList(1);
        $("#wx_pub_mgr").on("click","#pager button",function (e) {
            var text=e.target.innerHTML;
            if(Number(text)){
                if(SaimiriPagination.defaults.curPage!=Number(text)){
                    _this.queryList(Number(text));
                }
            }
        });
    },
    destroyed: function() {
        // 解决分页bug
        this.requery(1);
    },
    methods: {
        // 获取文章列表
        queryList: function(page, title) {
            var _this = this;
            $mks.syncJsonPost({
                data:{
                    page: page,
                    pageSize: SaimiriPagination.defaults.pageSize,
                    wxPubOriginId: _this.wxOriginid,
                    title: title
                },
                url:apibase + '/admin/wx/pub/material/news/list',
                success_func: function(res){
                    if(res.retCode == RetCode.SUCCESS) {
                        _this.wxNewsList = res.result;
                        $.extend(SaimiriPagination.defaults,{curPage:page, total:res.total, pageCount:Math.ceil(res.total/SaimiriPagination.defaults.pageSize)});
                        $("#pager").html(SaimiriPagination.paging());
                    } else {
                        alert(res.retMsg);
                        if (res.retCode == RetCode.NO_LOGIN) {
                            location.href = base + '/admin/login.html';
                        }
                    }
                }
            })
        },
        addUrl: function($event, id) {
            this.mapObj[id] = $event.target.value;
        },
        editNewsUrl: function() {
            var _this = this;
            $mks.syncJsonPost({
                data: {
                    'idUrl2Map': this.mapObj
                },
                url:apibase + '/admin/wx/pub/material/news/url/set',
                success_func: function(res){
                    if(res.retCode == RetCode.SUCCESS) {
                        alert("修改成功！")
                        _this.queryList(1);
                    } else {
                        alert(res.retMsg);
                        if (res.retCode == RetCode.NO_LOGIN) {
                            location.href = base + '/admin/login.html';
                        }
                    }
                }
            })
        },
        /**
         * 向前翻页
         */
        lastPage: function(){
            if (SaimiriPagination.defaults.curPage - 1 > 0) {
                SaimiriPagination.defaults.curPage -= 1;
                this.queryList(SaimiriPagination.defaults.curPage);
            }
        },
        /**
         * 向后翻页
         */
        nextPage: function(){
            if (SaimiriPagination.defaults.curPage +1 <=SaimiriPagination.defaults.pageCount){
                SaimiriPagination.defaults.curPage += 1;
                this.queryList(SaimiriPagination.defaults.curPage);
            }
        },
    },
    filters: {
        formateTimeStamp: function(time){
            return $mks.formatUnixTimeStamp(time);
        }
    }
}