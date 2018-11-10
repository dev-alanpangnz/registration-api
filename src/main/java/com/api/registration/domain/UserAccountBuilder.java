package com.api.registration.domain;

public final class UserAccountBuilder {
    private long userId;
    private String userName;
    private String email;
    private String emailVerified;
    private String password;
    private String passwordSalt;

    private UserAccountBuilder() { }

    public static UserAccountBuilder createuser() {
        return new UserAccountBuilder();
    }

    public UserAccountBuilder withUserId(long userId) {
        this.userId = userId;
        return this;
    }

    public UserAccountBuilder withUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public UserAccountBuilder withEmail(String email) {
        this.email = email;
        return this;
    }

    public UserAccountBuilder withEmailVerified(String emailVerified) {
        this.emailVerified = emailVerified;
        return this;
    }

    public UserAccountBuilder withPassword(String password) {
        this.password = password;
        return this;
    }

    public UserAccountBuilder withPasswordSalt(String passwordSalt) {
        this.passwordSalt = passwordSalt;
        return this;
    }

    public UserAccount build() {
        UserAccount userAccount = new UserAccount();
        userAccount.setUserId(userId);
        userAccount.setUserName(userName);
        userAccount.setEmail(email);
        userAccount.setEmailVerified(emailVerified);
        userAccount.setPassword(password);
        userAccount.setPasswordSalt(passwordSalt);
        return userAccount;
    }
}
