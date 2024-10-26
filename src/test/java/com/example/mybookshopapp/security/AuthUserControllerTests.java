package com.example.mybookshopapp.security;

import com.example.mybookshopapp.security.jwt.JWTBlacklistService;
import com.example.mybookshopapp.security.repositories.BookstoreUserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.Cookie;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author karl
 */

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
class AuthUserControllerTests {

    private final ObjectMapper objectMapper;
    private final BookstoreUserRepository bookstoreUserRepository;
    private final MockMvc mockMvc;
    private final JWTBlacklistService blacklistService;
    private RegistrationForm registrationForm;
    private ContactConfirmationPayload payload;
    private ContactConfirmationPayload payloadFail;

    @Autowired
    AuthUserControllerTests(ObjectMapper objectMapper, BookstoreUserRepository bookstoreUserRepository, MockMvc mockMvc, JWTBlacklistService blacklistService) {
        this.objectMapper = objectMapper;
        this.bookstoreUserRepository = bookstoreUserRepository;
        this.mockMvc = mockMvc;
        this.blacklistService = blacklistService;
    }

    @BeforeEach
    void setUp() {
        registrationForm = new RegistrationForm();
        registrationForm.setEmail("test@gmail.org");
        registrationForm.setName("Tester");
        registrationForm.setPass("iddqd");
        registrationForm.setPhone("9031232323");

        payload = new ContactConfirmationPayload();
        payload.setContact("email@email.net");
        payload.setCode("123456");

        payloadFail = new ContactConfirmationPayload();
        payloadFail.setContact("email@email.net");
        payloadFail.setCode("654321");
    }

    @AfterEach
    void tearDown() {
        registrationForm = null;
        payload = null;
    }

    @Transactional
    @Test
    void handleUserRegistration() throws Exception {
        mockMvc.perform(registerUserBuilder())
                .andDo(print())
                .andExpect(status().is2xxSuccessful());

        bookstoreUserRepository.deleteByEmail(registrationForm.getEmail());
    }

    @Test
    void handleUserRegistrationFail() throws Exception {
        mockMvc.perform(post("/reg")
                        .param("email", "email@email.net"))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    private RequestBuilder registerUserBuilder() {
        return post("/reg")
                .param("name", registrationForm.getName())
                .param("email", registrationForm.getEmail())
                .param("pass", registrationForm.getPass())
                .param("phone", registrationForm.getPhone());
    }

    @Test
    void handleLogin() throws Exception {
        mockMvc.perform(post("/login")
                .content(objectMapper.writeValueAsString(payload))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void handleLoginFail() throws Exception {
        mockMvc.perform(post("/login")
                        .content(objectMapper.writeValueAsString(payloadFail))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/signin"));
    }

    @Transactional
    @Test
    void handleLogout() throws Exception {
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzdGVwLnN2QG1haWwucnUiLCJleHAiOjE2ODgzODg3ODYsImlhdCI6MTY4ODM1Mjc4Nn0.X_LB9QOEi06vYeUYgvpRVbSZeIUiXpHuAKFG9w5Z2VY";
        mockMvc.perform(get("/logout").cookie(new Cookie("token", token)))
                .andDo(print())
                .andExpect(unauthenticated());
        assertTrue(blacklistService.tokenAtBlacklist(token));
        blacklistService.deleteFromBlacklist(token);
    }

    @Transactional
    @Test
    void handleLogoutAfterLogin() throws Exception {

        MvcResult mvcResult = mockMvc.perform(
                post("/login")
                        .content(objectMapper.writeValueAsString(payload))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        Cookie[] cookies = mvcResult.getResponse().getCookies();
        String token = "";
        for (Cookie cookie: cookies) {
            if (cookie.getName().equals("token")) {
                token = cookie.getValue();
                break;
            }
        }
        mockMvc.perform(get( "/logout").cookie(cookies))
                .andDo(print())
                .andExpect(unauthenticated());
        assertTrue(blacklistService.tokenAtBlacklist(token));
    }
}