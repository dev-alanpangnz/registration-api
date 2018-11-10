package com.api.registration;

import com.api.registration.services.EmailVerificationService;
import com.api.registration.services.EncryptionService;
import com.api.registration.services.LoginAuthenticationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.api.registration")
public class ServiceTestConfiguration {

    @Bean
    public EncryptionService encryptionService() {
        return new EncryptionService();
    }

    @Bean
    public LoginAuthenticationService loginAuthenticationService() {
        return new LoginAuthenticationService();
    }

    @Bean
    public EmailVerificationService emailVerificationService() {
        return new EmailVerificationService();
    }

}
