package com.api.registration.services;

import com.api.registration.domain.UserAccount;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.Random;

/**
 *  The purpose of this class is to implement a simple password hash.
 *  For simplicity reasons, I decided to use the java MD5 digest, which is not
 *  recommended in normal circumstances. The hash is 4 digits generated at random.
 *
 *  @author Alan Pang
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
