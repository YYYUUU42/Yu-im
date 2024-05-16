package com.yu.im.common.cache.config;

import cn.hutool.core.util.StrUtil;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

/**
 * @author yu
 * @description Redisson配置
 * @date 2024-05-16
 */
@Configuration
public class RedissonConfig {

    @Value("${spring.redis.address}")
    private String redisAddress;

    @Value("${spring.redis.password}")
    private String password;

    @Value("${spring.redis.database}")
    private int database;

    @Bean(name = "redissonClient")
    @ConditionalOnProperty(name = "redis.arrange.type", havingValue = "single")
    public RedissonClient singleRedissonClient() {
        Config config = new Config();
        SingleServerConfig singleServerConfig = config.useSingleServer();
        singleServerConfig.setAddress(redisAddress).setDatabase(database);
        if (!StrUtil.isEmpty(password)){
            singleServerConfig.setPassword(password);
        }
        return Redisson.create(config);
    }

    @Bean(name = "redissonClient")
    @ConditionalOnProperty(name = "redis.arrange.type", havingValue = "cluster")
    public RedissonClient clusterRedissonClient(){
        Config config = new Config();
        ClusterServersConfig clusterServersConfig = config.useClusterServers();
        clusterServersConfig.setNodeAddresses(Arrays.asList(redisAddress));
        if (!StrUtil.isEmpty(password)){
            clusterServersConfig.setPassword(password);
        }
        return Redisson.create(config);
    }
}
