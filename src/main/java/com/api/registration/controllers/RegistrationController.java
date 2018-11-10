package com.api.registration.controllers;

import com.api.registration.config.exceptions.UserNotFoundException;
import com.api.registration.config.exceptions.UserNotVerifiedException;
import com.api.registration.domain.UserAccount;
import com.api.registration.repository.UserAccountRepository;
import com.api.registration.services.EncryptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Random;

@RestController
public class RegistrationController {

    private final UserAccountRepository userAccountRepository;
    private final EncryptionService encryptionService;

    @Autowired
    public RegistrationController(
            UserAccountRepository userAccountRepository,
            EncryptionService encryptionService) {
        this.userAccountRepository = userAccountRepository;
        this.encryptionService = encryptionService;
    }

    @GetMapping("/username/{username}")
    UserAccount getUsername(@PathVariable String username) {
        return this.userAccountRepository.findByUserName(username)
                .orElseThrow(() -> new UserNotFoundException(username));
    }

    @RequestMapping(value = "/account/new", method = RequestMethod.POST)
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
}

