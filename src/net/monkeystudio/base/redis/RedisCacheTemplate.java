package net.monkeystudio.base.redis;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.monkeystudio.base.redis.bean.PipelineSource;
import net.monkeystudio.base.redis.bean.RedisDataSource;
import net.monkeystudio.base.redis.utils.SerializeUtils;
import net.monkeystudio.base.utils.Log;
import redis.clients.jedis.*;
import redis.clients.jedis.exceptions.JedisConnectionException;

/**
 * Created by linhongbin on 2017/5/31.
 */
@Service
public class RedisCacheTemplate {

    @Autowired
    private RedisDataSource redisDataSource;


    private String ip;
    private Integer port;
    private String password;

    //private Jedis jedis;
    private static JedisPool jedisPool;

    public static final String REDIS_CHARSET = "utf-8";

    public void setRedisDataSource(RedisDataSource redisDataSource) {
        this.redisDataSource = redisDataSource;
    }

    @PostConstruct
    private void init() {

        String ip = redisDataSource.getIp();
        Integer port = redisDataSource.getPort();
        String password = redisDataSource.getPassword();

        //打印初始化日志，密码不全显示
        String tmpPwd = password;
        if (password != null && password.length() > 2) {
            tmpPwd = password.substring(0, 2) + "****";
        }
        Log.i("Init redis pool parameters, ip=" + ip + ",port=" + port + ",password=" + tmpPwd);

        //保存参数
        this.ip = ip;
        this.port = port;
        this.password = password;


        //开始初始化连接池
        Log.i("Init redis pool ...");

        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(redisDataSource.getMaxTotal());
        config.setMaxIdle(redisDataSource.getMaxIdle());
        config.setMaxWaitMillis(redisDataSource.getMaxWaitMillis());  //获取连接时的最大等待毫秒数
        config.setTestOnBorrow(redisDataSource.getTestOnBorrow());  //在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
        config.setMinEvictableIdleTimeMillis(redisDataSource.getMinEvictableIdleTimeMillis());//逐出连接的最小空闲时间 默认1800000毫秒(30分钟)

        if (StringUtils.isBlank(password)) {
            jedisPool = new JedisPool(config, ip, port, redisDataSource.getTimeout());
        } else {
            jedisPool = new JedisPool(config, ip, port, redisDataSource.getTimeout(), password);
        }

    }


    /**
     * 获取redis连接池，如果不存在构建一个
     */
    protected synchronized JedisPool getPool() {

        if (jedisPool == null) {

            Log.i("Init redis pool ...");

            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxTotal(redisDataSource.getMaxTotal());
            config.setMaxIdle(redisDataSource.getMaxIdle());
            config.setMaxWaitMillis(redisDataSource.getMaxWaitMillis());  //获取连接时的最大等待毫秒数
            config.setTestOnBorrow(redisDataSource.getTestOnBorrow());  //在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
            config.setMinEvictableIdleTimeMillis(redisDataSource.getMinEvictableIdleTimeMillis());//逐出连接的最小空闲时间 默认1800000毫秒(30分钟)

            jedisPool = new JedisPool(config, ip, port, redisDataSource.getTimeout(), password);

        }

        return jedisPool;
    }


    public void setString(String key, String value) {

        JedisPool jedisPool = this.getPool();

        jedisPool.getNumActive();

        Jedis redis = getPool().getResource();


        try {
            redis.set(key, value);
        } catch (JedisConnectionException e) {
            Log.e("Jedis connection exception.");
        } catch (Exception e) {
            Log.e(e);
        } finally {
            redis.close();
        }
    }

    public void setStringWithExpire(String key, String value, Integer seconds) {

        Jedis redis = getPool().getResource();

        try {
            redis.set(key, value);
            redis.expire(key, seconds);
        } catch (JedisConnectionException e) {
            Log.e("Jedis connection exception.");
        } catch (Exception e) {
            Log.e(e);
        } finally {
            redis.close();
        }
    }

    public String getString(String key) {

        Jedis redis = getPool().getResource();

        try {
            return redis.get(key);
        } catch (JedisConnectionException e) {
            Log.e("Jedis connection exception.");
        } catch (Exception e) {
            Log.e(e);
        } finally {
            redis.close();
        }

        return null;
    }

    public Long expire(String key, Integer seconds) {

        Jedis redis = getPool().getResource();

        try {
            Long result = redis.expire(key, seconds);
            return result;
        } catch (JedisConnectionException e) {
            Log.e("Jedis connection exception.");
        } catch (Exception e) {
            Log.e(e.getMessage());
        } finally {
            redis.close();
        }

        return 0L;
    }

    /**
     * 设置对象
     *
     * @param key
     * @param value
     */
    public void setObject(String key, Object value) {

        Jedis redis = getPool().getResource();

        try {
            redis.set(key.getBytes(Charset.forName(REDIS_CHARSET)), SerializeUtils.serialize(value));
        } catch (JedisConnectionException e) {
            Log.e("Jedis connection exception.");
        } catch (Exception e) {
            Log.e(e);
        } finally {
            redis.close();
        }

    }

    public void hsetObject(String key, String field, Object object) {

        Jedis redis = getPool().getResource();

        try {
            redis.hset(key.getBytes(Charset.forName(REDIS_CHARSET)), field.getBytes(REDIS_CHARSET), SerializeUtils.serialize(object));
        } catch (JedisConnectionException e) {
            Log.e("Jedis connection exception.");
        } catch (Exception e) {
            Log.e(e);
        } finally {
            redis.close();
        }
    }


    /**
     * 获取对象
     *
     * @param key
     * @return
     */
    public <T> T getObject(String key) {

        Jedis redis = getPool().getResource();

        try {
            byte[] bytes = redis.get(key.getBytes(Charset.forName(REDIS_CHARSET)));
            if (bytes == null || bytes.length == 0) {
                return null;
            }

            T obj = (T) SerializeUtils.unserialize(bytes);

            return obj;

        } catch (JedisConnectionException e) {
            Log.e("Jedis connection exception.");
            return null;
        } catch (Exception e) {
            Log.e(e);
            return null;
        } finally {
            redis.close();
        }
    }

    /**
     * 计数加1
     *
     * @param key
     * @return
     */
    public Long incr(String key) {

        Jedis redis = getPool().getResource();
        Long result = 0L;

        try {
            result = redis.incr(key);
        } catch (JedisConnectionException e) {
            Log.e("Jedis connection exception.");
            return null;
        } catch (Exception e) {
            Log.e(e);
            return null;
        } finally {
            redis.close();
        }

        return result;
    }


    /**
     * 获取键列表
     *
     * @param keyWildcard
     * @return
     */
    public Set<String> keys(String keyWildcard) {

        Jedis redis = getPool().getResource();
        Set<String> keys = null;

        try {
            keys = redis.keys(keyWildcard);
        } catch (JedisConnectionException e) {
            Log.e("Jedis connection exception.");
            return null;
        } catch (Exception e) {
            Log.e(e);
            return null;
        } finally {
            redis.close();
        }

        return keys;
    }


    /**
     * 删除键
     *
     * @param key
     * @return
     */
    public Long del(String key) {

        Jedis redis = getPool().getResource();
        Long result = 0L;
        try {
            result = redis.del(key);
        } catch (JedisConnectionException e) {
            Log.e("Jedis connection exception.");
            return -1L;
        } catch (Exception e) {
            Log.e(e);
            return -1L;
        } finally {
            redis.close();
        }

        return result;
    }


    /**
     * 取集合
     *
     * @param key
     * @return
     */
    public Set<String> smembers(String key) {

        Jedis redis = getPool().getResource();

        try {
            return redis.smembers(key);
        } catch (JedisConnectionException e) {
            Log.e("Jedis connection exception.");
        } catch (Exception e) {
            Log.e(e);
        } finally {
            redis.close();
        }

        return null;
    }


    public void zAddString(String key, Long score, String value) {
        Jedis redis = getPool().getResource();
        try {
            redis.zadd(key.getBytes(Charset.forName(REDIS_CHARSET)), score, value.getBytes(Charset.forName(REDIS_CHARSET)));
        } catch (JedisConnectionException e) {
            Log.e("Jedis connection exception.");
        } catch (Exception e) {
            Log.e(e.getMessage());
        } finally {
            redis.close();
        }
    }


    public List<String> zRangeStringByScore(String key, Long min, Long max, Integer offset, Integer count) {

        Jedis redis = null;

        try {
            redis = getPool().getResource();
            Set<byte[]> zset = redis.zrangeByScore(key.getBytes(Charset.forName(REDIS_CHARSET)), min, max, offset, count);
            List<String> list = new ArrayList<>();

            for (byte[] bytes : zset) {
                if (bytes != null) {
                    String string = new String(bytes);
                    list.add(string);
                }
            }
            return list;

        } catch (JedisConnectionException e) {
            Log.e("Jedis connection exception.");
        } catch (Exception e) {
            Log.e(e);
        } finally {
            if (redis != null)
                redis.close();
        }

        return null;
    }


    public List<String> zRangeObjectByScore(String key, Long min, Long max) {

        Jedis redis = getPool().getResource();

        try {
            Set<byte[]> zset = redis.zrangeByScore(key.getBytes(Charset.forName(REDIS_CHARSET)), min, max);
            List<String> list = new ArrayList<>();

            for (byte[] bytes : zset) {
                if (bytes != null) {
                    String string = (String) SerializeUtils.unserialize(bytes);
                    list.add(string);
                }
            }

            return list;

        } catch (JedisConnectionException e) {
            Log.e("Jedis connection exception.");
        } catch (Exception e) {
            Log.e(e.getMessage());
        } finally {
            redis.close();
        }

        return null;
    }


    public List<String> zRangeObjectByScore(String key, Long min, Long max, Integer offset, Integer count) {
        Jedis redis = getPool().getResource();

        try {
            byte[] keyBytes = key.getBytes(Charset.forName(REDIS_CHARSET));
            Set<byte[]> zset = redis.zrangeByScore(keyBytes, min, max, offset, count);
            List<String> list = new ArrayList<>();

            for (byte[] bytes : zset) {
                if (bytes != null) {
                    String string = new String(bytes);
                    list.add(string);
                }
            }

            return list;

        } catch (JedisConnectionException e) {
            Log.e("Jedis connection exception.");
        } catch (Exception e) {
            Log.e(e.getMessage());
        } finally {
            redis.close();
        }

        return null;

    }

    public List<String> zrevrangeByScore(String key, Long min, Long max, Integer offset, Integer count) {

        Jedis redis = getPool().getResource();

        try {
            byte[] keyBytes = key.getBytes(Charset.forName(REDIS_CHARSET));
            Set<byte[]> zset = redis.zrevrangeByScore(keyBytes, max, min, offset, count);
            List<String> list = new ArrayList<>();

            for (byte[] bytes : zset) {
                if (bytes != null) {
                    String string = new String(bytes);
                    list.add(string);
                }
            }

            return list;

        } catch (JedisConnectionException e) {
            Log.e("Jedis connection exception.");
        } catch (Exception e) {
            Log.e(e.getMessage());
        } finally {
            redis.close();
        }

        return null;

    }


    public Long zRemRangeByScore(String key, Double start, Double end) {

        Jedis redis = getPool().getResource();

        Long result = 0L;

        try {
            result = redis.zremrangeByScore(key.getBytes(Charset.forName(REDIS_CHARSET)), start, end);
        } catch (JedisConnectionException e) {
            Log.e("Jedis connection exception.");
        } catch (Exception e) {
            Log.e(e.getMessage());
        } finally {
            redis.close();
        }

        return result;
    }

    public Long zRemByScore(String key, Double score) {
        return this.zRemRangeByScore(key, score, score);
    }

    public Long hset(String key, String field, String value) {

        Jedis redis = getPool().getResource();

        try {

            Long result = redis.hset(key, field, value);

            return result;

        } catch (JedisConnectionException e) {
            Log.e("Jedis connection exception.");
        } catch (Exception e) {
            Log.e(e.getMessage());
        } finally {
            redis.close();
        }

        return -1L;
    }

    /**
     * 计数加1
     *
     * @param key
     * @return
     */
    public Long hdel(String key, String field) {

        Jedis redis = getPool().getResource();
        Long result = 0L;

        try {
            result = redis.hdel(key, field);
        } catch (JedisConnectionException e) {
            Log.e("Jedis connection exception.");
            return null;
        } catch (Exception e) {
            Log.e(e.getMessage());
            return null;
        } finally {
            redis.close();
        }

        return result;
    }

    public <T> T hgetObject(String key, String field) {

        Jedis redis = getPool().getResource();

        try {
            byte[] bytes = redis.hget(key.getBytes(Charset.forName(REDIS_CHARSET)), field.getBytes(Charset.forName(REDIS_CHARSET)));
            if (bytes == null || bytes.length == 0) {
                return null;
            }

            T obj = (T) SerializeUtils.unserialize(bytes);

            return obj;

        } catch (JedisConnectionException e) {
            Log.e("Jedis connection exception.");
            return null;
        } catch (Exception e) {
            Log.e(e);
            return null;
        } finally {
            redis.close();
        }
    }


    public String hget(String key, String field) {

        Jedis redis = getPool().getResource();

        try {

            String value = redis.hget(key, field);

            return value;

        } catch (JedisConnectionException e) {
            Log.e("Jedis connection exception.");
        } catch (Exception e) {
            Log.e(e.getMessage());
        } finally {
            redis.close();
        }

        return null;
    }


    public Long hincrBy(String key, String field, Long value) {
        Jedis redis = getPool().getResource();
        Long result = 0L;

        try {
            result = redis.hincrBy(key, field, value);
        } catch (JedisConnectionException e) {
            Log.e("Jedis connection exception.");
            return null;
        } catch (Exception e) {
            Log.e(e.getMessage());
            return null;
        } finally {
            redis.close();
        }

        return result;
    }


    public Map<String, String> hgetAll(String key) {
        Jedis redis = getPool().getResource();
        Map<String, String> result = null;

        try {
            result = redis.hgetAll(key);
        } catch (JedisConnectionException e) {
            Log.e("Jedis connection exception.");
            return null;
        } catch (Exception e) {
            Log.e(e.getMessage());
            return null;
        } finally {
            redis.close();
        }

        return result;
    }


    public Transaction getTransaction() {
        Jedis redis = getPool().getResource();

        Transaction transaction = null;
        try {
            transaction = redis.multi();
        } catch (JedisConnectionException e) {
            Log.e("Jedis connection exception.");
            return null;
        } catch (Exception e) {
            Log.e(e.getMessage());
            return null;
        }

        return transaction;
    }


    public PipelineSource getPipelined() {
        Jedis redis = getPool().getResource();

        Pipeline pipelined = null;
        try {
            pipelined = redis.pipelined();
            PipelineSource pipelineSource = new PipelineSource(pipelined, redis);
            return pipelineSource;
        } catch (JedisConnectionException e) {
            Log.e("Jedis connection exception.");
        } catch (Exception e) {
            Log.e(e.getMessage());
        }
        return null;

    }

    public Boolean isExist(String key) {

        Jedis redis = getPool().getResource();
        Boolean result = null;
        try {
            result = redis.exists(key);
        } catch (JedisConnectionException e) {
            Log.e("Jedis connection exception.");
            return null;
        } catch (Exception e) {
            Log.e(e.getMessage());
            return null;
        } finally {
            redis.close();
        }

        return result;
    }


    public Transaction multi() {

        Jedis redis = getPool().getResource();
        Boolean result = null;
        try {
            Transaction tx = redis.multi();
        } catch (JedisConnectionException e) {
            Log.e("Jedis connection exception.");
            return null;
        } catch (Exception e) {
            Log.e(e.getMessage());
            return null;
        }

        return null;
    }

    public void publish(String channel ,String message) {
        Jedis jedis = getPool().getResource();
        try {
            jedis.publish(channel, message);
        }catch (JedisConnectionException e) {
            Log.e("Jedis connection exception.");
        } catch (Exception e) {
            Log.e(e.getMessage());
        }finally {
            jedis.close();
        }
    }

    public void subscribe(JedisPubSub jedisPubSub ,String channel) {
        Jedis jedis = getPool().getResource();
        try {
            jedis.subscribe(jedisPubSub, channel);
        }catch (JedisConnectionException e) {
            Log.e("Jedis connection exception.");
        } catch (Exception e) {
            Log.e(e.getMessage());
        }finally {
            jedis.close();
        }
    }


    /*public void flushAll(){

        Jedis redis = getPool().getResource();
        try{
            redis.flushAll();
        }catch(JedisConnectionException e) {
            Log.e("Jedis connection exception.");
        }catch (Exception e) {
            Log.e(e.getMessage());
        }
        finally {
            redis.close();
        }

    }*/

    /**
     *
     * @param key  hash的key
     * @return     key值对应域的数量,若key值不存在返回0
     *  -1:jedis connection exception  0:key不存在  >0:有值
     */
    public Long hLenByKey(String key) {
        Jedis redis = getPool().getResource();
        Long count = -1L;
        try {
            count = redis.hlen(key);
        } catch (JedisConnectionException e) {
            Log.e("Jedis connection exception.");
        } catch (Exception e) {
            Log.e(e);
        } finally {
            redis.close();
        }
        return count;
    }


}
