package com.api.registration.services;

import com.api.registration.domain.UserAccount;
import com.api.registration.repository.UserAccountRepository;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class EncryptionServiceTest {

//    private EncryptionService encryptionService = new EncryptionService();

    @Ignore
    @Test
    public void testForCorrectUserRegister() throws Exception {
        UserAccount userAccount = new UserAccount();
        userAccount.setUserName("a");
        userAccount.setEmail("b");
        userAccount.setEmailVerified("true");
        userAccount.setPassword("asd");
        userAccount.setSession("false");

        // Todo: Finish test class
    }
}
