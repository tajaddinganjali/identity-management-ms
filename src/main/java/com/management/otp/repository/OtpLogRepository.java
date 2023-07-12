package com.management.otp.repository;

import com.management.model.OtpLog;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OtpLogRepository extends JpaRepository<OtpLog, UUID> {

}
