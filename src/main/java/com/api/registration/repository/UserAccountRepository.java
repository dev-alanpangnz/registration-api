package com.api.registration.repository;

import com.api.registration.domain.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
    Optional<UserAccount> findByUserName(String username);
    Optional<UserAccount> findByEmail(String email);
    Optional<UserAccount> findByEmailVerified(String email);
    Optional<UserAccount> findByPassword(String password);
    Optional<UserAccount> findByPasswordSalt(String salt);
}
