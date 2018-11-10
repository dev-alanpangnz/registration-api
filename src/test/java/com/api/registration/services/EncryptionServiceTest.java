package com.api.registration.services;

import com.api.registration.domain.UserAccount;
import org.assertj.core.api.Assertions;
import org.junit.Test;

public class EncryptionServiceTest {

    private EncryptionService encryptionService = new EncryptionService();

    @Test
    public void testForCorrectUserRegister() throws Exception {
        UserAccount userAccount = new UserAccount();
        userAccount.setPassword("123");

        userAccount = encryptionService.hashAndSetUserAccountPassword(userAccount);
        Assertions.assertThat(userAccount.getPassword()).isNotEqualTo("123");
    }
}
