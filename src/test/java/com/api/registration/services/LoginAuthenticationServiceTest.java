package com.api.registration.services;

import com.api.registration.domain.UserAccount;
import org.assertj.core.api.Assertions;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.util.DigestUtils;

import java.util.Base64;

public class LoginAuthenticationServiceTest {

    private LoginAuthenticationService loginAuthenticationService = new LoginAuthenticationService();

    @Ignore
    @Test
    public void testForCorrectCredentialsComparison() throws Exception {
        byte[] encodedPassword = Base64.getEncoder().encode("123".getBytes());

        UserAccount registeredAccountPassword = new UserAccount();
        UserAccount incomingAccountPassword = new UserAccount();

        registeredAccountPassword.setPassword(new String(encodedPassword)+"1");
        registeredAccountPassword.setPasswordSalt("1");
        incomingAccountPassword.setPassword("123");

        boolean passwordCheck = loginAuthenticationService
                .authenticateUserCredentials(incomingAccountPassword, registeredAccountPassword);

        Assertions.assertThat(passwordCheck).isEqualTo(true);
    }

    @Test
    public void testForCorrectWorkingEncryptingLogic() throws Exception {
        UserAccount registeredAccountPassword = new UserAccount();
        UserAccount incomingAccountPassword = new UserAccount();

        registeredAccountPassword.setPassword("12345");
        incomingAccountPassword.setPassword("12345");

        boolean passwordCheck = loginAuthenticationService
                .authenticateUserCredentials(incomingAccountPassword, registeredAccountPassword);

        Assertions.assertThat(passwordCheck).isEqualTo(false);
    }
}
