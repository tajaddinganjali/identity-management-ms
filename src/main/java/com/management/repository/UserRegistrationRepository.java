package com.management.repository;

import com.management.model.User;
import com.management.model.UserRegistration;
import com.management.model.enums.RegistrationStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRegistrationRepository extends JpaRepository<UserRegistration, UUID> {

    List<UserRegistration> findAllByPhoneAndConsumerAndStatus(String phone, String consumer,
                                                              RegistrationStatus registrationStatus);

    Optional<UserRegistration> findByUserAndPhoneAndConsumerAndStatus(User user, String phone, String consumer,
                                                                      RegistrationStatus registrationStatus);

    @Query("SELECT distinct (u.user), u.phone from UserRegistration u where u.phone=:phone"
            + " and u.status=:status")
    List<User> findAllByPhoneAndStatus(String phone, RegistrationStatus status);

}
