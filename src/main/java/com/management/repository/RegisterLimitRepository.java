package com.management.repository;

import com.management.model.RegisterLimit;
import com.management.util.constant.SqlConstant;
import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RegisterLimitRepository extends DefaultRepository<RegisterLimit> {

    @Query(value = "SELECT * FROM REGISTER_LIMIT rl JOIN REGISTER_LIMIT_CONFIG rlc ON rl.CONFIG_ID=rlc.ID"
            + " WHERE rl.PIN=:pin AND rlc.service=:service", nativeQuery = true)
    Optional<RegisterLimit> findByPinAndService(@Param("pin") String pin,
                                                @Param("service") Integer service);

    @Query(value = "SELECT * FROM REGISTER_LIMIT rl JOIN REGISTER_LIMIT_CONFIG rlc ON rl.CONFIG_ID=rlc.ID"
            + " WHERE rl.PHONE=:phone AND rlc.service=:service", nativeQuery = true)
    Optional<RegisterLimit> findByPhoneAndService(String phone,
                                                  Integer service);

    @Query(value = SqlConstant.RESET_WRONG_REGISTER_ATTEMPTS_LOCK, nativeQuery = true)
    @Modifying
    void resetWrongRegisterAttemptsLock(@Param("service") Long service);

    @Query(value = SqlConstant.RESET_WRONG_REGISTER_ATTEMPT, nativeQuery = true)
    @Modifying
    void resetWrongRegisterAttempts(@Param("service") Long service);

}
