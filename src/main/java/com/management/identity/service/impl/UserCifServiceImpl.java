package com.management.identity.service.impl;

import com.management.identity.service.UserCifService;
import com.management.model.UserCif;
import com.management.repository.UserCifRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserCifServiceImpl implements UserCifService {

    private final UserCifRepository userCifRepository;

    @Override
    public List<UserCif> getCifsByPhone(String phone) {
        return userCifRepository.getCifsByPhone(phone);
    }

}
