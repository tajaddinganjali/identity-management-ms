package com.management.repository;

import com.management.model.AuthorizationLimit;
import com.management.util.constant.SqlConstant;
import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorizationLimitRepository extends DefaultRepository<AuthorizationLimit> {

    @Query(value = "SELECT * FROM AUTHORIZATION_LIMIT al "
            + "JOIN AUTHORIZATION_LIMIT_CONFIG alc ON al.CONFIG_ID=alc.ID "
            + "WHERE al.PHONE=:phone AND alc.SERVICE=:service", nativeQuery = true)
    Optional<AuthorizationLimit> findByPhoneAndService(@Param("phone") String phone,
                                                       @Param("service") Integer service);

    @Query(value = SqlConstant.RESET_LOCKED_AUTHORIZATION_ATTEMPT, nativeQuery = true)
    @Modifying
    void resetLockedAuthorizationAttempt(@Param("service") Long service);

    @Query(value = SqlConstant.RESET_WRONG_AUTHORIZATION_ATTEMPT, nativeQuery = true)
    @Modifying
    void resetWrongAuthorizationAttempt(@Param("service") Long service);

}
