package com.management.repository;

import com.management.model.Referral;
import com.management.model.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface ReferralRepository extends JpaRepository<Referral, UUID> {

    Optional<Referral> findByReferredUser(User user);

    @Modifying
    @Query(value = "UPDATE IBA_IAM_REFERRAL r set r.STATUS = 1 where r.REFERRED_USER_ID = :userId  AND r.STATUS = 0",
            nativeQuery = true)
    @Transactional
    int updateReferralStatus(@Param("userId") UUID userId);

}
