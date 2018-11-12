package com.api.registration.services;

import com.api.registration.domain.UserAccount;
import com.api.registration.repository.UserAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Random;

/**
 *  @author Alan Pang
 *  The purpose of this class is to implement a simple password hash
 *  For simplicity reasons, I decided not to use the Spring Security.
 *  Instead we're going to do a simple _ (Not recommended) + _
 */

@Service
public class EncryptionService {

    public UserAccount hashAndSetUserAccountPassword(UserAccount userAccount) {
        createAndSetUserPassword(userAccount);
        return userAccount;
    }

    private void createAndSetUserPassword(UserAccount input) {
        input.setPasswordSalt(createAndReturnRandomPasswordSalt());
        input.setPassword(hashPassword(input.getPassword(), input.getPasswordSalt()));
    }

    private String hashPassword(String password, String salt) {
        byte[] encodedPassword = Base64.getEncoder().encode(password.getBytes());
        return new String(encodedPassword)+salt;
    }

    private String createAndReturnRandomPasswordSalt() {
        return Integer
                .toString(new Random()
                        .ints(1000,9999)
                        .findFirst()
                        .getAsInt());
    }
}
