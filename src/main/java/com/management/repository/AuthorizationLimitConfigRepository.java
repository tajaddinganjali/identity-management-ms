package com.management.repository;

import com.management.model.AuthorizationLimitConfig;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorizationLimitConfigRepository extends DefaultRepository<AuthorizationLimitConfig> {

    @Query(value = "SELECT * FROM AUTHORIZATION_LIMIT_CONFIG alc WHERE alc.service=:service", nativeQuery = true)
    Optional<AuthorizationLimitConfig> findByEmbeddedLimitConfigService(@Param("service") Integer service);

}
