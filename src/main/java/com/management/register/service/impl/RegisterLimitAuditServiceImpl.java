package com.management.register.service.impl;

import com.management.model.RegisterLimitAud;
import com.management.register.service.RegisterLimitAuditService;
import com.management.repository.RegisterLimitAuditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RegisterLimitAuditServiceImpl implements RegisterLimitAuditService {

    private final RegisterLimitAuditRepository registerLimitAuditRepository;

    @Override
    public void save(RegisterLimitAud registerLimitAud) {
        registerLimitAuditRepository.save(registerLimitAud);
    }

}
