package com.api.registration.services;

import com.api.registration.domain.UserAccount;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.springframework.util.DigestUtils;

public class LoginAuthenticationServiceTest {

    private LoginAuthenticationService loginAuthenticationService = new LoginAuthenticationService();

    @Test
    public void testForCorrectCredentialsComparison() throws Exception {
        UserAccount registeredAccount = new UserAccount();
        UserAccount incommingAccount = new UserAccount();

        registeredAccount.setPasswordSalt("1234");
        byte[] passwordByte = "abc".getBytes();
        String registeredPassword = new String(DigestUtils.md5Digest(passwordByte)).toUpperCase();
        registeredAccount.setPassword(registeredPassword + registeredAccount.getPasswordSalt());
        incommingAccount.setPassword("abc");

        boolean passwordCheck = loginAuthenticationService
                .authenticateUserCredentials(incommingAccount, registeredAccount);

        Assertions.assertThat(passwordCheck).isEqualTo(true);
    }

    @Test
    public void testForCorrectWorkingSalt() throws Exception {
        UserAccount userA = new UserAccount();
        UserAccount userB = new UserAccount();

        userB.setPassword("12345");
        userA.setPassword("12345");

        boolean passwordCheck = loginAuthenticationService
                .authenticateUserCredentials(userA, userB);

        Assertions.assertThat(passwordCheck).isEqualTo(false);
    }
}
