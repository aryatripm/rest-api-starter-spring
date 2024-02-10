package id.arya.portofolio.users.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import id.arya.portofolio.users.config.JwtService;
import id.arya.portofolio.users.config.WebResponse;
import id.arya.portofolio.users.token.Token;
import id.arya.portofolio.users.token.TokenRepository;
import id.arya.portofolio.users.user.Role;
import id.arya.portofolio.users.user.User;
import id.arya.portofolio.users.user.UserRepository;
import id.arya.portofolio.users.user.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private JwtService jwtService;

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
    void getAllUsersSuccess() throws Exception {
        User user = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("password"))
                .email("admin@email.com")
                .role(Role.ADMIN)
                .build();
        var jwtToken = jwtService.generateToken(user);
        Token token = Token.builder()
                .user(user)
                .token(jwtToken)
                .expired(false)
                .revoked(false)
                .build();
        userRepository.save(user);
        tokenRepository.save(token);

        mockMvc.perform(
                get("/api/v1/users/")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
                    assertNull(result.getResolvedException());
                    assertNotNull(result.getResponse().getContentAsString());
                }
        );
    }

    @Test
    void getAllUsersFailedUnauthorized() throws Exception {
        User user = User.builder()
                .username("user")
                .password(passwordEncoder.encode("password"))
                .email("user@email.com")
                .role(Role.USER)
                .build();
        userRepository.save(user);
        mockMvc.perform(
                get("/api/v1/users/")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyIiwiaWF0IjoxNzA3NTQyNTAxLCJleHAiOjE3MDc2Mjg5MDF9.ugS2rG7ZD9Z0_dDUCxaHql7SHqrUerG_lYcNVlP_1ew")
        ).andExpectAll(
                status().isForbidden()
        );
    }

    @Test
    void getOneUserSuccess() throws Exception {
        User user = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("password"))
                .email("admin@email.com")
                .role(Role.ADMIN)
                .build();
        var jwtToken = jwtService.generateToken(user);
        Token token = Token.builder()
                .user(user)
                .token(jwtToken)
                .expired(false)
                .revoked(false)
                .build();
        userRepository.save(user);
        tokenRepository.save(token);

        mockMvc.perform(
                get("/api/v1/users/admin")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
                    WebResponse<UserResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNull(response.getErrors());
                    assertNotNull(response.getData().getUser());
                }
        );
    }
}