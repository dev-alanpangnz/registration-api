package com.api.registration.controllers;

import com.api.registration.config.exceptions.UserNotAuthenticatedException;
import com.api.registration.config.exceptions.UserNotFoundException;
import com.api.registration.config.exceptions.UserNotVerifiedException;
import com.api.registration.domain.UserAccount;
import com.api.registration.repository.UserAccountRepository;
import com.api.registration.services.EmailVerificationService;
import com.api.registration.services.EncryptionService;
import com.api.registration.services.LoginAuthenticationService;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
public class RegistrationController {

    private final UserAccountRepository userAccountRepository;
    private final EncryptionService encryptionService;
    private final EmailVerificationService emailVerificationService;
    private final LoginAuthenticationService loginAuthenticationService;

    @Autowired
    public RegistrationController(
            UserAccountRepository userAccountRepository,
            EncryptionService encryptionService,
            EmailVerificationService emailVerificationService,
            LoginAuthenticationService loginAuthenticationService) {
        this.userAccountRepository = userAccountRepository;
        this.encryptionService = encryptionService;
        this.emailVerificationService = emailVerificationService;
        this.loginAuthenticationService = loginAuthenticationService;
    }

    @RequestMapping(value = "/account/create", method = RequestMethod.POST)
    ResponseEntity<UserAccount> registerNewUser(@RequestBody UserAccount userAccount) {

        UserAccount newUser = encryptionService.registerUserIntoDatabase(userAccount);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{username}")
                .buildAndExpand(newUser.getUserName())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @RequestMapping(value = "/account/login", method = RequestMethod.POST)
    ResponseEntity<UserAccount> authenticateUser(@RequestBody UserAccount userAccount) {
        if (emailVerificationService.authenticateUserEmail(getUser(userAccount.getUserName()))) {
            if (loginAuthenticationService.authenticateUserCredentials(
                    userAccount,
                    getUser(userAccount.getUserName()))) {
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } else {
            throw new UserNotVerifiedException(userAccount.getEmail());
        }
    }

    @RequestMapping(value = "/account/update", method = RequestMethod.PUT)
    ResponseEntity<UserAccount> updateUserEmailAndPassword(@RequestBody UserAccount userAccount)
            throws NotFoundException {

        UserAccount currentUserData = getUser(userAccount.getUserName());

        if (!userAccount.getSession().equals("true")) {
            throw new UserNotAuthenticatedException(userAccount.getUserName());
        } else {
            currentUserData.setEmail(userAccount.getEmail());
            currentUserData.setPassword(userAccount.getPassword());
            userAccountRepository.save(currentUserData);
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    /**
     * Helper Method retrieving existing user, and throwing not found errors if no user is found
     * @param username the username of the User Account
     * @return User Object
     */
    private UserAccount getUser(String username) {
        UserAccount userAccount = userAccountRepository.findByUserName(username);
        if (userAccount == null) {
            throw new UserNotFoundException(username);
        }
        return userAccount;
    }
}

