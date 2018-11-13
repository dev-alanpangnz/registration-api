package com.api.registration.services;

import com.api.registration.domain.UserAccount;
import org.assertj.core.api.Assertions;
import org.junit.Test;

/**
 * This class is to test the MD5 one way encryption used to store passwords into
 * the database.
 *
 * @author Alan Pang
 */
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
