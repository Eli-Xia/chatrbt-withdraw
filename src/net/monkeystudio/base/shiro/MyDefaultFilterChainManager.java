package net.monkeystudio.base.shiro;

import org.apache.shiro.web.filter.mgt.DefaultFilterChainManager;

/**
 * Created by liujinhua on 2017/6/29.
 */
public class MyDefaultFilterChainManager extends DefaultFilterChainManager {

    @Override
    protected void addDefaultFilters(boolean init) {
        for (MyDefaultFilter defaultFilter : MyDefaultFilter.values()) {
            addFilter(defaultFilter.name(), defaultFilter.newInstance(), init, false);
        }
    }
}
