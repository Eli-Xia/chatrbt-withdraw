package net.monkeystudio.base.redis.bean;

/**
 * Created by bint on 2017/10/30.
 */
public class RedisDataSource {
    private String ip;
    private Integer port;
    private String password;
    private Integer maxTotal;
    private Integer maxIdle;
    private Long maxWaitMillis;
    private Boolean testOnBorrow;
    private Long minEvictableIdleTimeMillis;
    private Integer timeout;

    public Integer getTimeout() {
        return timeout;
    }
    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }
    public String getIp() {
        return ip;
    }
    public void setIp(String ip) {
        this.ip = ip;
    }
    public Integer getPort() {
        return port;
    }
    public void setPort(Integer port) {
        this.port = port;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public Integer getMaxTotal() {
        return maxTotal;
    }
    public void setMaxTotal(Integer maxTotal) {
        this.maxTotal = maxTotal;
    }
    public Integer getMaxIdle() {
        return maxIdle;
    }
    public void setMaxIdle(Integer maxIdle) {
        this.maxIdle = maxIdle;
    }
    public Long getMaxWaitMillis() {
        return maxWaitMillis;
    }
    public void setMaxWaitMillis(Long maxWaitMillis) {
        this.maxWaitMillis = maxWaitMillis;
    }
    public Boolean getTestOnBorrow() {
        return testOnBorrow;
    }
    public void setTestOnBorrow(Boolean testOnBorrow) {
        this.testOnBorrow = testOnBorrow;
    }
    public Long getMinEvictableIdleTimeMillis() {
        return minEvictableIdleTimeMillis;
    }
    public void setMinEvictableIdleTimeMillis(Long minEvictableIdleTimeMillis) {
        this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
    }
}