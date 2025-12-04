package com.example.productcatalog.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * DTO для данных ответа пользователя.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Ответ с информацией о пользователе")
public class UserResponseDTO {

    @Schema(description = "Идентификатор пользователя", example = "1")
    private Long id;

    @Schema(description = "Имя пользователя", example = "john_doe")
    private String username;

    @Schema(description = "Роль пользователя", example = "USER")
    private String userRole;
}
