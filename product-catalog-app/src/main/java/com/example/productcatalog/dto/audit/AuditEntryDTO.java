package com.example.productcatalog.dto.audit;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * DTO для данных ответа записи аудита.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Информация о записи аудита")
public class AuditEntryDTO {

    @Schema(description = "Идентификатор записи аудита", example = "1")
    private Long id;

    @Schema(description = "Временная метка действия", example = "2024-01-15T10:30:00")
    private String timestamp;

    @Schema(description = "Имя пользователя, выполнившего действие", example = "john_doe")
    private String username;

    @Schema(description = "Тип выполненного действия", example = "LOGIN")
    private String action;

    @Schema(description = "Дополнительные подробности действия", example = "Пользователь успешно вошел")
    private String details;
}
