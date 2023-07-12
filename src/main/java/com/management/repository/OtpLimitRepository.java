package com.management.repository;

import com.management.model.OtpLimit;
import com.management.util.constant.SqlConstant;
import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OtpLimitRepository extends DefaultRepository<OtpLimit> {

    @Query(value = SqlConstant.RESET_EXPIRED_OTP_ATTEMPT, nativeQuery = true)
    @Modifying
    void resetPerRoundWrongAttemptOtp(@Param("service") Long service);

    @Query(value = "SELECT * FROM OTP_LIMIT ol "
            + "JOIN OTP_LIMIT_CONFIG olc ON ol.CONFIG_ID=olc.ID "
            + "WHERE ol.PIN=:pin AND olc.SERVICE=:service", nativeQuery = true)
    Optional<OtpLimit> findByPinAndService(@Param("pin") String pin,
                                           @Param("service") Integer service);

}
