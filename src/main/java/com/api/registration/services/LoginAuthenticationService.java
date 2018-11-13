package com.api.registration.services;

import com.api.registration.domain.UserAccount;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

/**
 * The purpose of this class is to authenticate users using the same logic that was
 * used in the Encryption Service.
 *
 * @author Alan Pang
 */

@Service
public class LoginAuthenticationService {
    public boolean authenticateUserCredentials(UserAccount accountLoggingIn, UserAccount registeredAccount) {
        return passwordCheck(accountLoggingIn, registeredAccount);
    }

    private boolean passwordCheck(UserAccount incomingAccount, UserAccount registeredAccount) {
        byte[] passwordByte = incomingAccount.getPassword().getBytes();
        String md5Hex = new String(DigestUtils.md5Digest(passwordByte)).toUpperCase();
        String incomingPassword = md5Hex + registeredAccount.getPasswordSalt();
        return incomingPassword.equals(registeredAccount.getPassword());
    }
}
