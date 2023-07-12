package com.management;

import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication

@EnableFeignClients
@EnableJpaRepositories(basePackages = {"com.management.repository"})
@EntityScan({"com.identity.model"})
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "30m")
@EnableCaching
public class IdentityManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(IdentityManagementApplication.class, args);
    }

}
