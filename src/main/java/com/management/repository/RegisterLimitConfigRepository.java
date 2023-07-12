package com.management.repository;

import com.management.model.RegisterLimitConfig;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RegisterLimitConfigRepository extends DefaultRepository<RegisterLimitConfig> {

    @Query(value = "SELECT * FROM REGISTER_LIMIT_CONFIG rlc WHERE rlc.service=:service", nativeQuery = true)
    Optional<RegisterLimitConfig> findByEmbeddedLimitConfigService(
            @Param("service") Integer limitServiceType);

}
