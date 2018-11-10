package com.api.registration.services;

import com.api.registration.domain.UserAccount;
import org.springframework.stereotype.Service;

@Service
public class EmailVerificationService {
    public boolean authenticate(UserAccount userAccount) {
        return userAccount.getEmailVerified().equals("true");
    }
}
