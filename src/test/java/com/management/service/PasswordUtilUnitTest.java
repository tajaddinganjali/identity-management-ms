package com.management.service;

import com.management.util.PasswordUtil;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PasswordUtilUnitTest {

    @InjectMocks
    private PasswordUtil passwordUtil;

    @Test
    void testPassword() throws NoSuchAlgorithmException, InvalidKeySpecException {
        String pass = passwordUtil.hash("A1A1A1112", null);
        Assertions.assertNotNull(pass);
    }

}
