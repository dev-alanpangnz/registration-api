package com.api.registration.repository;

import com.api.registration.domain.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * UserAccount Data access level
 *
 * @author Alan Pang
 */
@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
    Optional<UserAccount> findByUserName(String username);
    UserAccount findDistinctByUserName(String username);
}
