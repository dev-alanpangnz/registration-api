package com.api.registration.controllers;

import com.api.registration.config.exceptions.EmailNotFoundException;
import com.api.registration.config.exceptions.UserNotAuthenticatedException;
import com.api.registration.config.exceptions.UserNotFoundException;
import com.api.registration.config.exceptions.UserNotVerifiedException;
import com.api.registration.domain.UserAccount;
import com.api.registration.repository.UserAccountRepository;
import com.api.registration.services.EmailSenderService;
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
import java.util.Random;

@RestController
public class RegistrationController {

    private final UserAccountRepository userAccountRepository;
    private final EncryptionService encryptionService;
    private final EmailVerificationService emailVerificationService;
    private final LoginAuthenticationService loginAuthenticationService;
    private final EmailSenderService emailSenderService;

    @Autowired
    public RegistrationController(
            UserAccountRepository userAccountRepository,
            EncryptionService encryptionService,
            EmailVerificationService emailVerificationService,
            LoginAuthenticationService loginAuthenticationService,
            EmailSenderService emailSenderService) {
        this.userAccountRepository = userAccountRepository;
        this.encryptionService = encryptionService;
        this.emailVerificationService = emailVerificationService;
        this.loginAuthenticationService = loginAuthenticationService;
        this.emailSenderService = emailSenderService;
    }

    @GetMapping("/account/{username}")
    ResponseEntity<UserAccount> checkIfUserExists(@PathVariable String username) {
        if (getUser(username) != null) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/account/create", method = RequestMethod.POST)
    ResponseEntity<UserAccount> createNewUserAndSendVerificationEmail(@RequestBody UserAccount userAccount) {
        UserAccount newUser = encryptionService.hashAndSetUserAccountPassword(userAccount);
        newUser.setVerificationCode(generateVerificationCode());
        userAccountRepository.save(newUser);
        // Commented out because TLS does not work with company firewall
        if (newUser.getEmail() != null) {
            emailSenderService.sendMail(newUser.getEmail(), newUser.getVerificationCode());
        } else {
            throw new EmailNotFoundException();
        }

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

    @RequestMapping(value = "/account/update/email", method = RequestMethod.PUT)
    ResponseEntity<UserAccount> updateUserEmail(@RequestBody UserAccount userAccount)
            throws NotFoundException {

        UserAccount currentUserData = getUser(userAccount.getUserName());

        currentUserData.setEmail(userAccount.getEmail());
        userAccountRepository.save(currentUserData);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/account/update/password", method = RequestMethod.PUT)
    ResponseEntity<UserAccount> updateUserPassword(@RequestBody UserAccount userAccount)
            throws NotFoundException {

        UserAccount currentUserData = getUser(userAccount.getUserName());
        encryptionService.hashAndSetUserAccountPassword(userAccount);
        currentUserData.setPassword(userAccount.getPassword());
        currentUserData.setPasswordSalt(userAccount.getPasswordSalt());
        userAccountRepository.save(currentUserData);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/account/verify", method = RequestMethod.PUT)
    ResponseEntity<UserAccount> verifyUserEmailOnSignUp(@RequestBody UserAccount userAccount) {
        UserAccount currentUserData = getUser(userAccount.getUserName());

        if (userAccount.getVerificationCode().equals(currentUserData.getVerificationCode())) {
            currentUserData.setEmailVerified("true");
            userAccountRepository.save(currentUserData);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * Helper Method for retrieving existing user, and throwing not found errors if no user is found
     * @param username the username of the User Account
     * @return User Object
     */
    private UserAccount getUser(String username) {
        return userAccountRepository.findByUserName(username)
                .orElseThrow(() -> new UserNotFoundException(username));
    }

    /**
     * Helper method for generating a verification code whenever a user tries to sign up
     * @return String code to put in the email
     */
    private String generateVerificationCode() {
        return Integer
                .toString(new Random()
                        .ints(1000,9999)
                        .findFirst()
                        .getAsInt());
    }
}

