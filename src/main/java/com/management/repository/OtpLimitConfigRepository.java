package com.management.repository;

import com.management.model.OtpLimitConfig;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OtpLimitConfigRepository extends DefaultRepository<OtpLimitConfig> {

    @Query(value = "SELECT * FROM OTP_LIMIT_CONFIG olc WHERE olc.SERVICE=:service", nativeQuery = true)
    Optional<OtpLimitConfig> findByService(@Param("service") Integer service);

}
