package com.example.productcatalog.controller;

import com.example.productcatalog.dto.ApiResponse;
import com.example.productcatalog.dto.user.UserResponseDTO;
import com.example.productcatalog.mapper.UserMapper;
import com.example.productcatalog.model.User;
import com.example.productcatalog.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST контроллер для операций управления пользователями.
 * <p>
 * Предоставляет конечные точки для получения информации о пользователях.
 * Требуется роль ADMIN для доступа.
 * </p>
 *
 */
@Slf4j
@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Конечные точки управления пользователями (только для администраторов)")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    /**
     * Получает всех пользователей.
     * <p>
     * Эта конечная точка доступна только администраторам.
     * </p>
     *
     * @param userRole роль запрашивающего пользователя
     * @return список всех пользователей
     */
    @GetMapping
    @Operation(summary = "Получить всех пользователей", description = "Получает всех пользователей (только для администраторов)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Пользователи получены"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Доступ запрещен")
    })
    public ResponseEntity<ApiResponse<List<UserResponseDTO>>> getAllUsers(
            @RequestHeader(value = "X-User-Role", defaultValue = "USER") String userRole) {
        log.debug("Получение всех пользователей, запрошено ролью: {}", userRole);

        if (!"ADMIN".equals(userRole)) {
            throw new SecurityException("Доступ запрещен. Требуется роль администратора.");
        }

        List<User> users = userService.getAllUsers();
        List<UserResponseDTO> dtos = userMapper.toDTOList(users);

        return ResponseEntity.ok(ApiResponse.success(dtos));
    }
}
