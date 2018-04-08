var settlementCenter = new Vue({
    el: "#settlement-wrap",
    data: {
        availableAmount: null, //可提现金额
        alreadyWithdrawAmount: null,
        totalAmount: null,
        withdrawMoney: null, // 我的提现额
        switchVal: 0, // 提现弹窗可提现金额
        minAmount: 0, // 最小可提现值
        withdrawRecord: [],
        incomeDetail: {},
        detailModel: false,
        withdrawModel: false,
        tabId: 1,
        settingStatus: 3, // 1: create, 2: edit, 3: submitted
        accountType: 1, //1: 公司账户  2：个人账户
        imgsrc: '',
        provinces: [],
        cities: [],
        proCities: [],
        banks: [],
        accountInfo: {},
        myAccount: {},
        errInfo: '',
        isCollapse: false, //发票示例是否展开
    },
    components: {
        'k-radio': Radio,
        'k-select': Select,
        'k-option': Option,
        'k-cascader': Cascader
    },
    watch: {
        'accountInfo.provinceCode': function(newValue, oldValue) {
            var _this = this;
            this.proCities = [];
            this.cities.map(function(item) {
                if (item.proCode === newValue) {
                    _this.proCities.push(item);
                }
            })
        },
    },
    created: function () {
        this.queryWithdraw();
        this.resetAccount();
        this.querySettingInfo();
    },
    methods: {
        changeSection: function() {
            this.sectionId == id;
        },
        /***
         * 收益管理
        ***/
        queryWithdraw: function() {
            $("#list_layer").show();
            var _this = this;
            $mks.syncJsonPost({
                url: apibase + "/money-withdraw/page",
                success_func: function (res) {
                    $("#list_layer").hide();
                    if (res.retCode == RetCode.SUCCESS) {
                        var result = res.result;
                        _this.availableAmount = result.availableAmount;
                        _this.alreadyWithdrawAmount = result.alreadyWithdrawAmount;
                        _this.totalAmount = result.totalAmount;
                        _this.minAmount = result.minAmount;
                        _this.switchVal = result.switchVal;
                        _this.withdrawRecord = result.moneyWithdrawRecordList;
                    } else {
                        dialog(res.retMsg, 2);
                    }
                }
            });
        },
        withdrawHandler: function() {
            var _this = this;
            var withdrawMoney = parseFloat(this.withdrawMoney);
            var data = {
                "amount": withdrawMoney
            }
            if (withdrawMoney >= this.minAmount) {
                $mks.syncJsonPost({
                    url: apibase + "/money-withdraw/apply",
                    data: data,
                    success_func: function (res) {
                        if (res.retCode == RetCode.SUCCESS) {
                            dialog("提现成功", 1)
                            _this.toggleWithdrawModel();
                            _this.queryWithdraw();
                        } else {
                            dialog(res.retMsg, 2);
                        }
                    }
                });
            } else {
                dialog('可提现金额至少'+_this.minAmount+'元', 2);
            }
        },
        toggleDetailModel: function(id) {
            if (this.detailModel) {
                $("#global_layer").hide();
                this.detailModel = false
            } else {
                $("#global_layer").show();
                this.detailModel = true;
                this.queryIncomeDetail(id);
            }
        },
        queryIncomeDetail: function(id) {
            var _this = this;
            $mks.syncJsonPost({
                url: apibase + "/money-withdraw/" + id,
                success_func: function (res) {
                    if (res.retCode == RetCode.SUCCESS) {
                        _this.incomeDetail = res.result;
                    } else {
                        dialog(res.retMsg, 2);
                    }
                }
            });
        },
        formatAmount: function(value) {
            this.withdrawMoney = value
                .trim()
                .slice(
                0,
                value.indexOf('.') === -1
                    ? value.length
                    : value.indexOf('.') + 3
                );
            this.withdrawMoney = Number(this.withdrawMoney);
            if (this.withdrawMoney > this.availableAmount) {
                this.withdrawMoney = this.availableAmount;
            }
        },
        toggleWithdrawModel: function(id) {
            if (this.withdrawModel) {
                $("#global_layer").hide();
                this.withdrawModel = false
            } else {
                $("#global_layer").show();
                this.withdrawModel = true;
            }
        },
        /***
         * 结算中心
        ***/
        // 更改select组件value
        changeSelect: function(value, code) {
            this.accountInfo[value] = code;
        },
        querySettingInfo: function() {
            var _this = this;
            $mks.syncJsonPost({
                url: apibase + "/settlement/settings",
                success_func: function (res) {
                    if (res.retCode == RetCode.SUCCESS) {
                        var result = res.result;
                        _this.provinces = result.provinces;
                        _this.cities = result.cities;
                        _this.banks = result.banks;
                        if (result.accountSettingResp.id === null) {
                            _this.settingStatus = 1;
                        } else {
                            _this.settingStatus = 3;
                            _this.accountInfo.accountType = result.accountSettingResp.accountType;
                            _this.myAccount = result.accountSettingResp;
                            var resetImg = document.getElementById('sensitive-pic');
                            if (resetImg) {
                                resetImg.src = '/api/settlement/sensitive-pic?id='+Math.random();
                            }
                        }
                    } else {
                        dialog(res.retMsg, 2);
                    }
                }
            });
        },
        queryUserInfo: function() {
            var _this = this;
            $mks.syncJsonPost({
                url: apibase + "/settlement/settings",
                success_func: function (res) {
                    if (res.retCode == RetCode.SUCCESS) {
                        var result = res.result;
                        if (result.accountSetting === null) {
                            _this.settingStatus = 1;
                        } else {
                            _this.settingStatus = 3;
                        }
                    }
                }
            });
        },
        //点击修改按钮
        modifyAccount: function() {
            this.settingStatus = 2;
            this.accountInfo = Object.assign({}, this.accountInfo, this.myAccount);
            this.accountInfo.bankId = this.myAccount.bank.id;
            this.accountInfo.provinceCode = this.myAccount.province.code;
            this.accountInfo.cityCode = this.myAccount.city.code;
            this.imgsrc = '';
        },
        cancelModify: function() {
            this.settingStatus = 3;
            this.accountInfo.accountType = this.myAccount.accountType;
            this.modalShowError("");
        },
        resetAccount: function() {
            this.accountInfo = {
                id: null, //新增id为null,修改带id
                image: null,
                accountType: 0, //公司账户:0 个人账户;1
                accountHolder: null, //开户人
                idNumber: null, //开户人身份证号
                accountName: '', //开户名称
                bankId: null, //银行id
                provinceCode: null, //省code
                cityCode: null, //市code
                bankForkName: '', //支行名称
                accountNumber: null, //银行账号
                email: ''
            }
        },
        previewImg: function() {
            var _this = this;
            var preview = document.getElementById("preview");
            var file = document.getElementById("upload").files[0];
            var maxsize = 5 * 1024 * 1024; // 图片最大5M
            this.modalShowError("");
            // 接受 jpeg, jpg, png 类型的图片
            if (!/\/(?:jpeg|jpg|png)/i.test(file.type)) {
                dialog('请上传jpg、jpeg、png格式', 2);
                return;
            }
            // 限制文件大小
            if(file.size > maxsize) {
                dialog('图片需小于5M', 2);
                return;
            }
            var reader = new FileReader();
            if (file) {  
                reader.readAsDataURL(file);  
            } else {  
                _this.imgsrc = '';  
            } 
            reader.onloadend = function(){  
                _this.imgsrc = reader.result;  
            } 
        },
        //保存修改
        saveHandler: function() {
            var data = this.accountInfo;
            var _this = this;
            this.modalShowError("");
            if (this.errInfo.trim().length > 0) return;
            if (!data.accountName || !data.bankId || !data.provinceCode || !data.cityCode 
                || !data.bankForkName || !data.accountName || !data.email || !document.getElementById('confirmPwd').value) {
                // dialog("请填写必要信息！", 2);
                _this.modalShowError("请填写必要信息！");
                return;
            }
            if ( data.accountType == '1' ) {
                var file = document.getElementById('upload').files[0];
                if (file) {
                    data.image = file;
                } else {
                    _this.modalShowError("请上传委托人收款证明");
                    return;
                }
            }
            if (data.accountType == '1' && (!data.accountHolder || !data.idNumber)) {
                _this.modalShowError("请填写必要信息！");
                return;
            }
            var formData = new FormData();
            // if ()
            this.addFormData(formData);
            $.ajax({
                url: apibase + '/settlement/settings/add/update',
                type: 'POST',
                data: formData,
                cache: false,
                processData: false,
                contentType: false,
                success: function(res) {
                    _this.showLoading = false;
                    if (res.retCode == RetCode.SUCCESS) {
                        dialog("上传成功!", 1);
                        _this.querySettingInfo();
                    } else {
                        dialog(res.retMsg, 2);
                    }
                }
            });
        },
        //身份证位数校验
        idCardTest: function(event) {
            var reg = /(^\d{15}$)|(^\d{17}(\d|X)$)/;
            var idCard = Number(event.target.value);
            var el = event.target, _this = this;
            if (reg.test(idCard)) {
                el.classList.remove('has-error');
                el.classList.add('has-success');
                _this.modalShowError("");
            } else {
                el.classList.remove('has-success');
                el.classList.add('has-error');
                _this.modalShowError('请填写正确的身份证');
            }
        },
        checkData: function(event, msg) {
            var _this = this;
            var el = event.target;
            if( $mks.strIsEmpty(el.value) ) {
                el.classList.remove('has-success');
                el.classList.add('has-error');
                _this.modalShowError('请填写正确的' + msg);
            } else {
                el.classList.remove('has-error');
                el.classList.add('has-success');
                _this.modalShowError("");
            }
        },
        checkAccountNum: function(event) {
            var _this = this;
            var el = event.target;
            if( !$mks.strIsEmpty(el.value) && el.value === _this.accountInfo.accountNumber ) {
                el.classList.remove('has-error');
                el.classList.add('has-success');
                _this.modalShowError("");
            } else {
                el.classList.remove('has-success');
                el.classList.add('has-error');
                _this.modalShowError('两次输入的银行账号不一致！');
            }
        },
        addFormData: function(formData) {
            var data = this.accountInfo;
            if (data.accountType == '1') {
                formData.append("image", data.image);
            }
            //新建不传id
            if (this.settingStatus == 1) {
                // formData.append("id", null);
            }
            //修改需传id
            if (this.settingStatus == 2) {
                formData.append("id", data.id);
            }
            formData.append("accountType", data.accountType);
            formData.append("accountHolder", data.accountHolder);
            formData.append("idNumber", data.idNumber);
            formData.append("accountNumber", data.accountNumber);
            formData.append("accountName", data.accountName);
            formData.append("bankId", data.bankId);
            formData.append("provinceCode", data.provinceCode);
            formData.append("cityCode", data.cityCode);
            formData.append("bankForkName", data.bankForkName);
            formData.append("email", data.email);
        },
        modalShowError: function(info) {
            this.errInfo = info;
        }
    },
    filters: {
        formatDateTime: function (time) {
            return $mks.formatDateTime(time);
        },
    }
})