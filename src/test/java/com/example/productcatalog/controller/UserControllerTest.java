package com.example.productcatalog.controller;

import com.example.productcatalog.dto.UserResponseDTO;
import com.example.productcatalog.mapper.UserMapper;
import com.example.productcatalog.model.User;
import com.example.productcatalog.model.enums.UserRole;
import com.example.productcatalog.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * WebMVC тесты для UserController.
 */
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private UserMapper userMapper;

    @Test
    @DisplayName("Должен вернуть всех пользователей для администратора")
    void getAllUsers_ShouldReturnUsers_WhenAdmin() throws Exception {
        List<User> users = Arrays.asList(
                User.builder().id(1L).username("user1").userRole(UserRole.USER).build(),
                User.builder().id(2L).username("admin").userRole(UserRole.ADMIN).build()
        );

        List<UserResponseDTO> dtos = Arrays.asList(
                UserResponseDTO.builder().id(1L).username("user1").userRole("USER").build(),
                UserResponseDTO.builder().id(2L).username("admin").userRole("ADMIN").build()
        );

        when(userService.getAllUsers()).thenReturn(users);
        when(userMapper.toDTOList(users)).thenReturn(dtos);

        mockMvc.perform(get("/users")
                        .header("X-User-Role", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    @DisplayName("Должен вернуть ошибку 401 для обычных пользователей")
    void getAllUsers_ShouldReturn401_WhenNotAdmin() throws Exception {
        mockMvc.perform(get("/users")
                        .header("X-User-Role", "USER"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Должен вернуть ошибку 401, когда отсутствует заголовок роли")
    void getAllUsers_ShouldReturn401_WhenNoRoleHeader() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isUnauthorized());
    }
}
