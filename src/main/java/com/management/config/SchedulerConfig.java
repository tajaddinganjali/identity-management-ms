package com.management.config;

import com.management.util.constant.ExecutorConstant;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.redis.jedis.JedisLockProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;


@Configuration
@EnableAsync
@RequiredArgsConstructor
public class SchedulerConfig {

    private final Environment environment;
    @Value("${schedule.lock.redis-host}")
    private String host;
    @Value("${schedule.lock.redis-port}")
    private Integer port;
    @Value("${schedule.lock.redis-timeout}")
    private Integer timeout;
    @Value("${schedule.lock.redis-password}")
    private String password;

    private static ThreadPoolTaskExecutor createThreadPoolTaskExecutor(int queueCapacity, int corePoolSize,
                                                                       int maxPoolSize, String threadNamePrefix) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setQueueCapacity(queueCapacity);
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setThreadNamePrefix(threadNamePrefix);
        executor.initialize();
        return executor;
    }

    /**
     * Creates and returns executor with specified details.
     *
     * @return {@code TaskExecutor} with modified configurations
     * @see TaskExecutor
     */
    @Bean(ExecutorConstant.RESET_WRONG_OTP_ATTEMPT)
    public ThreadPoolTaskExecutor resetWrongOtpAttempt() {
        int queueCapacity =
                Objects.requireNonNull(
                        environment.getProperty("identity.executor.reset-wrong-attempt.queue-capacity",
                                Integer.class));
        int corePoolSize =
                Objects.requireNonNull(
                        environment.getProperty("identity.executor.reset-wrong-attempt.core-pool-size",
                                Integer.class));
        int maxPoolSize =
                Objects.requireNonNull(
                        environment.getProperty("identity.executor.reset-wrong-attempt.max-pool-size",
                                Integer.class));
        return createThreadPoolTaskExecutor(
                queueCapacity, corePoolSize, maxPoolSize, ExecutorConstant.RESET_WRONG_OTP_ATTEMPT);
    }


    @Bean(name = "jedisPoolConfig")
    public JedisPoolConfig jedisPoolConfig(@Value("${jedis.pool.maxTotal}") int maxTotal,
                                           @Value("${jedis.pool.maxIdle}") int maxIdle,
                                           @Value("${jedis.pool.maxWaitMillis}") int maxWaitMillis) {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(maxTotal);
        config.setMaxIdle(maxIdle);
        config.setMaxWaitMillis(maxWaitMillis);
        return config;
    }

    @Bean
    public LockProvider lockProvider(JedisPoolConfig jedisPoolConfig) {
        JedisPool jedisPool = new JedisPool(jedisPoolConfig, host, port, timeout, password);
        return new JedisLockProvider(jedisPool);
    }

}
