package net.monkeystudio.portal.controller.resp.accountsetting;

import net.monkeystudio.chatrbtw.entity.AccountSetting;
import net.monkeystudio.chatrbtw.entity.Bank;
import net.monkeystudio.chatrbtw.entity.City;
import net.monkeystudio.chatrbtw.entity.Province;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xiaxin
 */
public class AccountSettingVO {

    private AccountSettingResp accountSettingResp;

    private List<Province> provinces = new ArrayList<>();

    private List<City> cities = new ArrayList<>();

    private List<Bank> banks = new ArrayList<>();

    public AccountSettingResp getAccountSettingResp() {
        return accountSettingResp;
    }

    public void setAccountSettingResp(AccountSettingResp accountSettingResp) {
        this.accountSettingResp = accountSettingResp;
    }

    public List<Province> getProvinces() {
        return provinces;
    }

    public void setProvinces(List<Province> provinces) {
        this.provinces = provinces;
    }

    public List<City> getCities() {
        return cities;
    }

    public void setCities(List<City> cities) {
        this.cities = cities;
    }

    public List<Bank> getBanks() {
        return banks;
    }

    public void setBanks(List<Bank> banks) {
        this.banks = banks;
    }
}
