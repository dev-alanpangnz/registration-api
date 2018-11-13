package com.api.registration.repository;

import com.api.registration.domain.UserAccount;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

/**
 * This class is aimed at just testing the data access.
 *
 * @author Alan Pang
 */
@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserAccountRepositoryTest {

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testJpaRepositoryForGettingDistinctUser() {
        UserAccount storedUser = new UserAccount();
        storedUser.setUserName("user");
        entityManager.persist(storedUser);
        entityManager.flush();

        UserAccount queriedUser = userAccountRepository
                .findDistinctByUserName(storedUser.getUserName());

        Assertions.assertThat(queriedUser.getUserName()).isEqualTo(storedUser.getUserName());
    }

    @Test
    public void testJpaRepositoryForGettingUserWithoutDistinct() {
        UserAccount storedUser = new UserAccount();
        storedUser.setUserName("user");
        entityManager.persist(storedUser);
        entityManager.flush();

        Optional<UserAccount> queriedUser = userAccountRepository
                .findByUserName(storedUser.getUserName());

        Assertions.assertThat(queriedUser.get().getUserName())
                .isEqualTo(storedUser.getUserName());
    }
}
