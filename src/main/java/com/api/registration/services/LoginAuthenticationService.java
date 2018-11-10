package com.api.registration.services;

import com.api.registration.domain.UserAccount;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
public class LoginAuthenticationService {
    public boolean authenticateUserCredentials(UserAccount accountLoggingIn, UserAccount registeredAccount) {
        return passwordCheck(accountLoggingIn, registeredAccount);
    }

    private boolean passwordCheck(UserAccount incomingAccount, UserAccount registeredAccount) {
        byte[] encodedPassword = Base64.getEncoder().encode(incomingAccount.getPassword().getBytes());
        String incomingPassword = new String(encodedPassword) + registeredAccount.getPasswordSalt();
        return incomingPassword.equals(registeredAccount.getPassword());
    }
}
