package com.api.registration.services;

import com.api.registration.config.exceptions.UserNotVerifiedException;
import com.api.registration.domain.UserAccount;
import com.api.registration.repository.UserAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;

import java.util.Random;

/**
 *  @author Alan Pang
 *  The purpose of this class is to implement a simple password hash
 *  For simplicity reasons, I decided not to use the Spring Security.
 *  Instead we're going to do a simple _ (Not recommended) + _
 */

@Service
public class EncryptionService {

    private UserAccountRepository userAccountRepository;

    @Autowired
    public EncryptionService(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    public UserAccount registerUserIntoDatabase(UserAccount userAccount) {
        UserAccount newUser;
        if (userAccount.getEmailVerified().equals("true")) {
            createAndSetUserPassword(userAccount);
            newUser = userAccountRepository.save(userAccount);
        } else {
            throw new UserNotVerifiedException(userAccount.getEmail());
        }

        userAccountRepository.save(userAccount);
        return newUser;
    }

    private void createAndSetUserPassword(UserAccount input) {
        input.setPasswordSalt(createAndReturnRandomPasswordSalt());
        input.setPassword(hashPassword(input.getPassword(), input.getPasswordSalt()));
    }

    private String hashPassword(String password, String salt) {
        // Byte Strings galore, for now lets password+salt
        return password+salt;
    }

    private String createAndReturnRandomPasswordSalt() {
        return Integer
                .toString(new Random()
                        .ints(0,100)
                        .findFirst()
                        .getAsInt());
    }
}
