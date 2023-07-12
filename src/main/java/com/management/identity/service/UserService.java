package com.management.identity.service;

import com.management.identity.enums.ProfileType;
import com.management.model.Consumer;
import com.management.model.User;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface UserService {

    User save(User user);

    User updateUser(User userFromIamDb, User userFromProvider, Consumer consumer);

    Optional<User> findByPin(String pin);

    Optional<User> findUserById(String userId);

    User findActiveUserById(String userId);

    void setUserPassCode(String userId, String passcode, String consumerId, String lang, String deviceId,
                         boolean agreementSigned)
            throws InvalidKeySpecException, NoSuchAlgorithmException;

    void saveUserCifPhonesToCache(String userId, String verificationToken, Map<String, List<String>> userPhoneCifList,
                                  boolean isPreviousCifsMustBeDeleted);

    void saveUserCifPhonesToDatabase(String userId, String verificationToken, ProfileType profileType);

    void auditAndDeletePreviousUserCifs(User user);

    void saveCifPhonesToDb(Map<String, List<String>> userPhoneCifs, User user);

}
