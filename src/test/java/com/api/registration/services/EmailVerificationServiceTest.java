package com.api.registration.services;

import com.api.registration.domain.UserAccount;
import org.assertj.core.api.Assertions;
import org.junit.Test;

public class EmailVerificationServiceTest {

    private EmailVerificationService emailVerificationService = new EmailVerificationService();

    UserAccount verifiedEmail = new UserAccount();
    UserAccount unverifiedEmail = new UserAccount();

    @Test
    public void testForVerifiedEmail() throws Exception {
        verifiedEmail.setEmailVerified("true");
        boolean verification = emailVerificationService
                .authenticateUserEmail(verifiedEmail);

        Assertions.assertThat(verification).isTrue();
    }

    @Test
    public void testForUnverifiedEmail() throws Exception {
        unverifiedEmail.setEmailVerified("false");
        boolean verification = emailVerificationService
                .authenticateUserEmail(unverifiedEmail);

        Assertions.assertThat(verification).isFalse();
    }
}
