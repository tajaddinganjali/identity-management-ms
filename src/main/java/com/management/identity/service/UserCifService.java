package com.management.identity.service;

import com.management.model.UserCif;
import java.util.List;

public interface UserCifService {

    List<UserCif> getCifsByPhone(String phone);

}
