package id.arya.portofolio.users.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import id.arya.portofolio.users.auth.LoginRequest;
import id.arya.portofolio.users.auth.LoginResponse;
import id.arya.portofolio.users.auth.RegisterRequest;
import id.arya.portofolio.users.config.WebResponse;
import id.arya.portofolio.users.token.Token;
import id.arya.portofolio.users.token.TokenRepository;
import id.arya.portofolio.users.user.Role;
import id.arya.portofolio.users.user.User;
import id.arya.portofolio.users.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        tokenRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void loginFailedUserNotFound() throws Exception {
        LoginRequest request = LoginRequest.builder()
                .username("user")
                .password("password")
                .build();

        mockMvc.perform(
                        post("/api/v1/auth/login")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpectAll(
                        status().isUnauthorized()
                );
    }

    @Test
    void loginFailedWrongPassword() throws Exception {
        userRepository.save(
                User.builder()
                        .username("user")
                        .password(passwordEncoder.encode("password"))
                        .email("user@email.com")
                        .role(Role.USER)
                        .build()
        );

        LoginRequest request = LoginRequest.builder()
                .username("user")
                .password("wrong_password")
                .build();

        mockMvc.perform(
                        post("/api/v1/auth/login")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpectAll(
                        status().isForbidden()
                );
    }

    @Test
    void loginSuccess() throws Exception {
        userRepository.save(
                User.builder()
                        .username("user")
                        .password(passwordEncoder.encode("password"))
                        .email("user@email.com")
                        .role(Role.USER)
                        .build()
        );

        LoginRequest request = LoginRequest.builder()
                .username("user")
                .password("password")
                .build();

        mockMvc.perform(
                        post("/api/v1/auth/login")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpectAll(
                        status().isOk()
                ).andDo(result -> {
                    WebResponse<LoginResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNull(response.getErrors());
                    assertNotNull(response.getData().getToken());
                    assertNotNull(response.getData().getRefreshToken());

                    User userDb = userRepository.findById("user").orElse(null);
                    assertNotNull(userDb);
                });
    }

    @Test
    void registerFailedUserExist() throws Exception {
        userRepository.save(
                User.builder()
                        .username("user")
                        .password(passwordEncoder.encode("password"))
                        .email("user@email.com")
                        .role(Role.USER)
                        .build()
        );

        RegisterRequest request = RegisterRequest.builder()
                .username("user")
                .password("password")
                .email("user@email.com")
                .build();

        mockMvc.perform(
                        post("/api/v1/auth/register")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpectAll(
                        status().isBadRequest()
                );
    }

    @Test
    void registerSuccess() throws Exception {
        userRepository.save(
                User.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("password"))
                        .email("admin@email.com")
                        .role(Role.ADMIN)
                        .build()
        );

        RegisterRequest request = RegisterRequest.builder()
                .username("user")
                .password("password")
                .email("user@email.com")
                .role(Role.USER)
                .build();

        mockMvc.perform(
                        post("/api/v1/auth/register")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpectAll(
                        status().isOk()
                ).andDo(result -> {
                    User userDb = userRepository.findById("user").orElse(null);
                    assertNotNull(userDb);
                });
    }

    @Test
    void logoutSuccess() throws Exception {
        User user = User.builder()
                .username("user")
                .password(passwordEncoder.encode("password"))
                .email("user@email.com")
                .role(Role.USER)
                .build();
        Token token = Token.builder()
                .user(user)
                .token("token")
                .expired(false)
                .revoked(false)
                .build();
        userRepository.save(user);
        tokenRepository.save(token);

        mockMvc.perform(
                post("/api/v1/auth/logout")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer token")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
                    List<Token> tokenDb = tokenRepository.findAllValidTokenByUser("user");
                    assertEquals(tokenDb.size(), 0);
                }
        );
    }
}