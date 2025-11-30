package com.example.productcatalog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * DTO для запроса входа.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Данные запроса входа")
public class LoginRequestDTO {

    @NotBlank(message = "Имя пользователя обязательно")
    @Size(min = 3, max = 50, message = "Имя пользователя должно быть от 3 до 50 символов")
    @Schema(description = "Имя пользователя", example = "john_doe", required = true)
    private String username;

    @NotBlank(message = "Пароль обязателен")
    @Size(min = 1, max = 100, message = "Пароль должен быть от 4 до 100 символов")
    @Schema(description = "Пароль", example = "password123", required = true)
    private String password;
}
