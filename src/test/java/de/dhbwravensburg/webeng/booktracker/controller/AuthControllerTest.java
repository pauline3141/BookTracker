package de.dhbwravensburg.webeng.booktracker.controller;

import de.dhbwravensburg.webeng.booktracker.model.User;
import de.dhbwravensburg.webeng.booktracker.security.JwtAuthFilter;
import de.dhbwravensburg.webeng.booktracker.security.JwtUtil;
import de.dhbwravensburg.webeng.booktracker.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

    @Test
    void register_returns200WithToken_whenValid() throws Exception {
        User savedUser = new User(1L, "testuser", "encodedPassword");
        when(userService.register("testuser", "secret123")).thenReturn(savedUser);
        when(jwtUtil.generateToken("testuser")).thenReturn("fake-jwt-token");

        String body = """
                {
                    "username": "testuser",
                    "password": "secret123"
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("fake-jwt-token"))
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    void register_returns400_whenUsernameTooShort() throws Exception {
        String body = """
                {
                    "username": "ab",
                    "password": "secret123"
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.username").exists());
    }

    @Test
    void register_returns400_whenPasswordTooShort() throws Exception {
        String body = """
                {
                    "username": "testuser",
                    "password": "123"
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.password").exists());
    }

    @Test
    void login_returns200WithToken_whenCredentialsValid() throws Exception {
        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(jwtUtil.generateToken("testuser")).thenReturn("fake-jwt-token");

        String body = """
                {
                    "username": "testuser",
                    "password": "secret123"
                }
                """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("fake-jwt-token"))
                .andExpect(jsonPath("$.username").value("testuser"));
    }
}
