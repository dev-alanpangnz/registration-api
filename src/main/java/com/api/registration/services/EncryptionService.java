package com.api.registration.services;

import com.api.registration.domain.UserAccount;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.Random;

/**
 *  @author Alan Pang
 *  The purpose of this class is to implement a simple password hash
 *  For simplicity reasons, I decided not to use the Spring Security.
 *  Instead we're going to do a simple MD5digest (Not recommended) +
 *  4 random digits
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
        byte[] passwordByte = password.getBytes();
        String md5Hex = new String(DigestUtils.md5Digest(passwordByte)).toUpperCase();
        return md5Hex+salt;
    }

    private String createAndReturnRandomPasswordSalt() {
        return Integer
                .toString(new Random()
                        .ints(1000,9999)
                        .findFirst()
                        .getAsInt());
    }
}
