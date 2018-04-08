package net.monkeystudio.wx.vo.thirtparty;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by bint on 2017/10/27.
 */
public class FuncInfoItem {

    @JsonProperty("funcscope_category")
    private FuncscopeCategory funcscopeCategory;

    @JsonProperty("confirm_info")
    private ConfirmInfo confirmInfo;

    public FuncscopeCategory getFuncscopeCategory() {
        return funcscopeCategory;
    }

    public void setFuncscopeCategory(FuncscopeCategory funcscopeCategory) {
        this.funcscopeCategory = funcscopeCategory;
    }
}
