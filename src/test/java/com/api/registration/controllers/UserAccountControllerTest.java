package com.api.registration.controllers;
import com.api.registration.domain.UserAccount;
import com.api.registration.repository.UserAccountRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserAccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private UserAccountRepository userAccountRepository;

    private UserAccount user1 = new UserAccount();
    private UserAccount user2 = new UserAccount();

    @Test
    public void a_TestCreatingVerifiedUser() throws Exception {
        user1.setUserName("usable");
        user1.setPassword("12345");
        user1.setEmail("test@user.com");
        user1.setEmailVerified("true");

        this.mockMvc.perform(post("/account/create")
                .content(mapper.writeValueAsString(user1))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    public void b_TestCreatingUnverifiedUser() throws Exception {
        user2.setUserName("John");
        user2.setPassword("Smith");
        user2.setEmail("test@user.com");
        user2.setEmailVerified("false");

        this.mockMvc.perform(post("/account/create")
                .content(mapper.writeValueAsString(user2))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    public void c_testCreatingAccountWithExistingUser() throws Exception {
        user2 = userAccountRepository.findDistinctByUserName("John");
        this.mockMvc.perform(post("/account/create")
                .content(mapper.writeValueAsString(user2))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    public void d_testVerifyingUserEmailViaToken() throws Exception {
        user2 = userAccountRepository.findDistinctByUserName("John");
        user2.setPassword("Smith");
        user2.setVerificationCode(user2.getVerificationCode());

        this.mockMvc.perform(put("/account/verify")
                .content(mapper.writeValueAsString(user2))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void e_testForLoginWithWrongPassword() throws Exception {
        user2.setPassword("password");
        this.mockMvc.perform(put("/account/login")
                .content(mapper.writeValueAsString(user2))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    public void f_testUpdateUserEmail() throws Exception {
        user2 = userAccountRepository.findDistinctByUserName("John");
        user2.setEmail("test@user2.com");
        this.mockMvc.perform(put("/account/update/email")
                .content(mapper.writeValueAsString(user2))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void g_testUpdateUserPassword() throws Exception {
        user2 = userAccountRepository.findDistinctByUserName("John");
        user2.setPassword("12345");
        this.mockMvc.perform(put("/account/update/password")
                .content(mapper.writeValueAsString(user2))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }
}

