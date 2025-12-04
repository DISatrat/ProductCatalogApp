package com.example.productcatalog.controller;

import com.example.productcatalog.dto.user.LoginRequestDTO;
import com.example.productcatalog.dto.user.RegisterRequestDTO;
import com.example.productcatalog.mapper.UserMapper;
import com.example.productcatalog.model.User;
import com.example.productcatalog.model.enums.UserRole;
import com.example.productcatalog.service.audit.AuditService;
import com.example.productcatalog.service.user.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * WebMVC тесты для AuthController.
 */
@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @Mock
    private AuditService auditService;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private AuthController authController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    @DisplayName("Должен успешно зарегистрировать нового пользователя")
    void register_ShouldReturnCreatedUser() throws Exception {
        RegisterRequestDTO request = RegisterRequestDTO.builder()
                .username("newuser")
                .password("password123")
                .build();

        User user = User.builder()
                .id(1L)
                .username("newuser")
                .userRole(UserRole.USER)
                .build();

        when(userService.register(anyString(), anyString())).thenReturn(user);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Пользователь успешно зарегистрирован"));

        verify(userService).register("newuser", "password123");
        verify(auditService).record(eq("newuser"), eq("REGISTER"), anyString());
    }

    @Test
    @DisplayName("Должен вернуть ошибку запроса, когда имя пользователя отсутствует")
    void register_ShouldReturnBadRequest_WhenUsernameMissing() throws Exception {
        RegisterRequestDTO request = RegisterRequestDTO.builder()
                .password("password123")
                .build();

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Должен успешно войти с действительными учетными данными")
    void login_ShouldReturnUser_WhenCredentialsValid() throws Exception {
        LoginRequestDTO request = LoginRequestDTO.builder()
                .username("testuser")
                .password("password123")
                .build();

        User user = User.builder()
                .id(1L)
                .username("testuser")
                .passwordHash("password123".hashCode() + "")
                .userRole(UserRole.USER)
                .build();

        when(userService.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(userService.checkPassword("password123", user.getPasswordHash())).thenReturn(true);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Вход успешен"));
    }

    @Test
    @DisplayName("Должен вернуть ошибку аутентификации, когда пользователь не найден")
    void login_ShouldReturnUnauthorized_WhenUserNotFound() throws Exception {
        LoginRequestDTO request = LoginRequestDTO.builder()
                .username("nonexistent")
                .password("password123")
                .build();

        when(userService.findByUsername("nonexistent")).thenReturn(Optional.empty());

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Должен успешно выйти из системы")
    void logout_ShouldReturnSuccess() throws Exception {
        mockMvc.perform(post("/auth/logout")
                        .param("username", "testuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Выход успешен"));

        verify(auditService).record(eq("testuser"), eq("LOGOUT"), anyString());
    }
}