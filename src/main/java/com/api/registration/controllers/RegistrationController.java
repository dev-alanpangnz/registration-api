package com.api.registration.controllers;

import com.api.registration.config.exceptions.UserNotVerifiedException;
import com.api.registration.domain.UserAccount;
import com.api.registration.repository.UserAccountRepository;
import com.api.registration.services.EmailVerificationService;
import com.api.registration.services.EncryptionService;
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

    @Autowired
    public RegistrationController(
            UserAccountRepository userAccountRepository,
            EncryptionService encryptionService,
            EmailVerificationService emailVerificationService) {
        this.userAccountRepository = userAccountRepository;
        this.encryptionService = encryptionService;
        this.emailVerificationService = emailVerificationService;
    }

    @RequestMapping(value = "/account/create", method = RequestMethod.POST)
    ResponseEntity<UserAccount> registerNewUser(@RequestBody UserAccount userAccount) {

        UserAccount newUser = encryptionService.registerUserIntoDatabase(userAccount);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{username}")
                .buildAndExpand(newUser.getUserName())
                .toUri();

        return ResponseEntity
                .created(location)
                .build();
    }

    @RequestMapping(value = "/account/login", method = RequestMethod.POST)
    ResponseEntity<UserAccount> authenticateUser(@RequestBody UserAccount userAccount) {
        if (emailVerificationService.authenticate(userAccount)) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            throw new UserNotVerifiedException(userAccount.getEmail());
        }
    }

    @RequestMapping(value = "/account/{username}/update", method = RequestMethod.PUT)
    ResponseEntity<UserAccount> updateUserEmailAndPassword(@RequestBody UserAccount userAccount)
            throws NotFoundException {

        UserAccount currentUserData = getUser(userAccount.getUserName());

        if (!userAccount.getSession().equals("true")) {
            throw new UserNotVerifiedException(userAccount.getEmail());
        } else {
            currentUserData.setEmail(userAccount.getEmail());
            currentUserData.setPassword(userAccount.getPassword());
            userAccountRepository.save(currentUserData);
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    private UserAccount getUser(String username) throws NotFoundException {
        UserAccount userAccount = userAccountRepository.findByUserName(username);

        if (userAccount == null) {
            throw new NotFoundException("User not found");
        }

        return userAccount;
    }
}

