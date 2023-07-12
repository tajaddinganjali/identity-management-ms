package com.management.repository;

import com.management.model.Device;
import com.management.model.Session;
import com.management.model.User;
import com.management.model.enums.SessionStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface SessionRepository extends JpaRepository<Session, UUID> {

    List<Session> findSessionsByUserAndDevice(User user, Device device);

    Optional<Session> findByIdAndStatus(UUID id, SessionStatus sessionStatus);

    @Modifying
    @Transactional
    @Query("update Session s set s.status = 0 "
            + "where s.user =:user "
            + "and s.device =:device "
            + "and s.status = 1")
    void deactivateActiveSessionsByUserAndDevice(User user, Device device);

}

