package com.management.identity.service.impl;

import com.management.identity.service.UserRegistrationService;
import com.management.model.UserRegistration;
import com.management.repository.UserRegistrationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserRegistrationServiceImpl implements UserRegistrationService {

    private final UserRegistrationRepository userRegistrationRepository;

    @Override
    public UserRegistration save(UserRegistration userRegistration) {
        return userRegistrationRepository.save(userRegistration);
    }

}
