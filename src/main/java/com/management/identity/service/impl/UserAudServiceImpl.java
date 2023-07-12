package com.management.identity.service.impl;

import com.management.identity.service.UserAudService;
import com.management.model.User;
import com.management.model.UserAud;
import com.management.repository.UserAudRepository;
import java.time.OffsetDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserAudServiceImpl implements UserAudService {

    private final UserAudRepository userAudRepository;

    @Autowired
    public UserAudServiceImpl(UserAudRepository userAudRepository) {
        this.userAudRepository = userAudRepository;
    }

    @Override
    public void save(User user) {
        UserAud userAud = UserAud.builder()
                .cardNumber(user.getCardNumber())
                .cif(user.getCif())
                .gender(user.getGender())
                .user(user)
                .identityIssuingDate(user.getIdentityIssuingDate())
                .identityMaturityDate(user.getIdentityMaturityDate())
                .identityNumber(user.getIdentityNumber())
                .identityType(user.getIdentityType())
                .dateOfBirth(user.getDateOfBirth())
                .maritalStatus(user.getMaritalStatus())
                .name(user.getName())
                .passcode(user.getPasscode())
                .phone(user.getPhone())
                .pin(user.getPin())
                .status(user.getStatus())
                .surname(user.getSurname())
                .version(user.getVersion())
                .updatedAt(user.getUpdatedAt())
                .createdAt(OffsetDateTime.now())
                .resident(user.getResident())
                .build();

        save(userAud);
    }

    private void save(UserAud userAud) {
        userAudRepository.save(userAud);
    }

}
