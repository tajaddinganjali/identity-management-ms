package com.management.repository;

import com.management.model.UserAud;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAudRepository extends JpaRepository<UserAud, UUID> {

}
