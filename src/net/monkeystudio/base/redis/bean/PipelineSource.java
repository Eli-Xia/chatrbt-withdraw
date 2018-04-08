package net.monkeystudio.base.redis.bean;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

/**
 * Created by bint on 2017/10/31.
 */
public class PipelineSource {
    private Pipeline pipeline;
    private Jedis jedis;

    public PipelineSource(Pipeline pipeline ,Jedis jedis){
        this.pipeline = pipeline;
        this.jedis = jedis;
    }

    public Pipeline getPipeline() {
        return pipeline;
    }

    public void setPipeline(Pipeline pipeline) {
        this.pipeline = pipeline;
    }

    public Jedis getJedis() {
        return jedis;
    }

    public void setJedis(Jedis jedis) {
        this.jedis = jedis;
    }
}
