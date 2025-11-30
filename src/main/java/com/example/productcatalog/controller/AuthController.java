package com.example.productcatalog.controller;

import com.example.productcatalog.dto.*;
import com.example.productcatalog.mapper.UserMapper;
import com.example.productcatalog.model.User;
import com.example.productcatalog.service.AuditService;
import com.example.productcatalog.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST контроллер для операций аутентификации.
 * <p>
 * Предоставляет конечные точки для регистрации, входа и выхода пользователя.
 * </p>
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Конечные точки аутентификации и регистрации пользователя")
public class AuthController {

    private final UserService userService;
    private final AuditService auditService;
    private final UserMapper userMapper;

    /**
     * Регистрирует нового пользователя.
     *
     * @param request запрос на регистрацию
     * @return созданный пользователь
     */
    @PostMapping("/register")
    @Operation(summary = "Зарегистрировать нового пользователя", description = "Создает новый аккаунт пользователя")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Пользователь успешно зарегистрирован"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Неверный запрос"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Имя пользователя уже существует")
    })
    public ResponseEntity<ApiResponse<UserResponseDTO>> register(@Valid @RequestBody RegisterRequestDTO request) {
        log.info("Запрос на регистрацию пользователя: {}", request.getUsername());

        User user = userService.register(request.getUsername(), request.getPassword());
        auditService.record(request.getUsername(), "REGISTER", "Пользователь успешно зарегистрирован");

        UserResponseDTO dto = userMapper.toDTO(user);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(dto, "Пользователь успешно зарегистрирован"));
    }

    /**
     * Аутентифицирует пользователя.
     *
     * @param request запрос на вход
     * @return аутентифицированный пользователь
     */
    @PostMapping("/login")
    @Operation(summary = "Вход пользователя", description = "Аутентифицирует пользователя с именем пользователя и паролем")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Вход успешен"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Неверные учетные данные")
    })
    public ResponseEntity<ApiResponse<UserResponseDTO>> login(@Valid @RequestBody LoginRequestDTO request) {
        log.info("Запрос на вход пользователя: {}", request.getUsername());

        User user = userService.findByUsername(request.getUsername())
                .orElseThrow(() -> new SecurityException("Неверное имя пользователя или пароль"));

        if (!userService.checkPassword(request.getPassword(), user.getPasswordHash())) {
            auditService.record(request.getUsername(), "LOGIN_FAILED", "Неверный пароль");
            throw new SecurityException("Неверное имя пользователя или пароль");
        }

        auditService.record(request.getUsername(), "LOGIN", "Пользователь успешно вошел");

        UserResponseDTO dto = userMapper.toDTO(user);
        return ResponseEntity.ok(ApiResponse.success(dto, "Вход успешен"));
    }

    /**
     * Выходит из учетной записи пользователя.
     *
     * @param username имя пользователя, который будет выведен
     * @return сообщение об успехе
     */
    @PostMapping("/logout")
    @Operation(summary = "Выход пользователя", description = "Выводит указанного пользователя")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Выход успешен")
    })
    public ResponseEntity<ApiResponse<Void>> logout(@RequestParam String username) {
        log.info("Запрос на выход пользователя: {}", username);

        auditService.record(username, "LOGOUT", "Пользователь вышел из системы");

        return ResponseEntity.ok(ApiResponse.success(null, "Выход успешен"));
    }
}
