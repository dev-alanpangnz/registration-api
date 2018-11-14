package com.api.registration.controllers;

import com.api.registration.config.exceptions.*;
import com.api.registration.domain.UserAccount;
import com.api.registration.repository.UserAccountRepository;
import com.api.registration.services.EmailSenderService;
import com.api.registration.services.EmailVerificationService;
import com.api.registration.services.EncryptionService;
import com.api.registration.services.LoginAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Random;

/**
 * Bespoke REST controller for registering a user and logging in. This controller contains
 * endpoint which will be used from the frontend.
 *
 * @author Alan Pang
 */
@RestController
public class UserAccountController {

    private final UserAccountRepository userAccountRepository;
    private final EncryptionService encryptionService;
    private final EmailVerificationService emailVerificationService;
    private final LoginAuthenticationService loginAuthenticationService;
    private final EmailSenderService emailSenderService;

    @Autowired
    public UserAccountController(
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

    /**
     * This method registers a new user with the EmailVerified field of false
     * @param userAccount the payload containing: Username, Email, Password and EmailVerified: false
     * @return ResponseEntity
     */
    @RequestMapping(value = "/account", method = RequestMethod.POST)
    ResponseEntity<UserAccount> createNewUserAndSendVerificationEmail(@RequestBody UserAccount userAccount) {

        // Handle Existing Users: Resend verification to unactivated users or throw error
        if (userAccountRepository.findDistinctByUserName(userAccount.getUserName()) != null
                && userAccountRepository.findDistinctByUserName(userAccount.getUserName()).getEmailVerified().equals("false")) {
            UserAccount unactivatedUser = userAccountRepository.findDistinctByUserName(userAccount.getUserName());
            unactivatedUser.setVerificationCode(generateVerificationCode());
            userAccountRepository.save(unactivatedUser);
            emailSenderService.sendMail(unactivatedUser.getEmail(), unactivatedUser.getVerificationCode());
            return new ResponseEntity<>(HttpStatus.OK);
        } else if (userAccountRepository.findDistinctByUserName(userAccount.getUserName()) != null) {
            throw new UserAlreadyExistsException(userAccount.getUserName());
        }

        UserAccount newUser = encryptionService.hashAndSetUserAccountPassword(userAccount);
        newUser.setVerificationCode(generateVerificationCode());
        userAccountRepository.save(newUser);

        checkIfEmailIsEmptyBeforeSendingVerificationCode(newUser);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{username}")
                .buildAndExpand(newUser.getUserName())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    /**
     * Re-sends an email with a newly generated verification code. This allows the user to get
     * another code if their email was lost or if they did not receive their email.
     * @param userAccount username to be used to get the unactivated user.
     * @return OK
     */
    @RequestMapping(value = "/account/verify", method = RequestMethod.POST)
    ResponseEntity<UserAccount> resendEmailWithVerificationCode(@RequestBody UserAccount userAccount) {
        UserAccount currentUserData = getUser(userAccount.getUserName());
        currentUserData.setVerificationCode(generateVerificationCode());
        userAccountRepository.save(currentUserData);
        checkIfEmailIsEmptyBeforeSendingVerificationCode(currentUserData);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * A secondary API call after calling the unverified creation method.
     * This method checks if the code in the server sent to the user has been correctly received
     * and entered from the client side. If it has, then the User account has been verified and is
     * allowed to log in.
     * @param userAccount Payload containing username and verification code from the frontend
     * @return ResponseEntity
     */
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
     * Allows the client to log in by authenticating their credentials.
     * This method checks whether the User is allowed to log in by peeking at emailVerified.
     * If true, method proceeds to authenticate user credentials.
     * @param userAccount containing username and password
     * @return ResponseEntity
     */
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

    /**
     * Updates user account (Merging email and password)
     * Better practice instead of naming uri with specific name
     * @param userAccount containing username and email or password
     * @return
     */
    @RequestMapping(value = "/account", method = RequestMethod.PUT)
    ResponseEntity<UserAccount> userUserProfile(@RequestBody UserAccount userAccount) {

        UserAccount currentUserData = getUser(userAccount.getUserName());

        if (userAccount.getEmail() != null) {
            currentUserData.setEmail(userAccount.getEmail());
        }

        if (userAccount.getPassword() != null) {
            encryptionService.hashAndSetUserAccountPassword(userAccount);
            currentUserData.setPassword(userAccount.getPassword());
            currentUserData.setPasswordSalt(userAccount.getPasswordSalt());
        }

        userAccountRepository.save(currentUserData);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Allows the client to make changes to their Emails. Simple call, this endpoint is called
     * after the user has logged in and wants to make a change to their email.
     * @param userAccount containing username and email
     * @return
     */
    @RequestMapping(value = "/account/email", method = RequestMethod.PUT)
    ResponseEntity<UserAccount> updateUserEmail(@RequestBody UserAccount userAccount) {

        UserAccount currentUserData = getUser(userAccount.getUserName());

        currentUserData.setEmail(userAccount.getEmail());
        userAccountRepository.save(currentUserData);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Allows the client to make changes to their passwords, same format as above but with
     * password.
     * @param userAccount payload containing username and password
     * @return ResponseEntity
     */
    @RequestMapping(value = "/account/password", method = RequestMethod.PUT)
    ResponseEntity<UserAccount> updateUserPassword(@RequestBody UserAccount userAccount) {

        UserAccount currentUserData = getUser(userAccount.getUserName());
        encryptionService.hashAndSetUserAccountPassword(userAccount);
        currentUserData.setPassword(userAccount.getPassword());
        currentUserData.setPasswordSalt(userAccount.getPasswordSalt());
        userAccountRepository.save(currentUserData);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Helper Method for retrieving existing user, and throwing not found exception if no user is found
     * @param username the username of the User Account
     * @return UserAccount Object of the specified user.
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

    private void checkIfEmailIsEmptyBeforeSendingVerificationCode(UserAccount newUser) {
        if (newUser.getEmail() != null) {
            emailSenderService.sendMail(newUser.getEmail(), newUser.getVerificationCode());
        } else {
            throw new EmailNotFoundException();
        }
    }
}

