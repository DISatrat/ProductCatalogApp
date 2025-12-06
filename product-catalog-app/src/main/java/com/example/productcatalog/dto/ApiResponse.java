package com.example.productcatalog.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * Универсальная обертка ответа API.
 *
 * @param <T> тип данных в ответе
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Обертка ответа API")
public class ApiResponse<T> {

    @Schema(description = "Указывает, была ли операция успешной", example = "true")
    private boolean success;

    @Schema(description = "Сообщение ответа", example = "Операция выполнена успешно")
    private String message;

    @Schema(description = "Данные ответа")
    private T data;

    @Schema(description = "Подробности ошибки (если есть)")
    private String error;

    /**
     * Создает успешный ответ с данными.
     *
     * @param data    данные ответа
     * @param message сообщение об успехе
     * @param <T>     тип данных
     * @return успешный ApiResponse
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }

    /**
     * Создает успешный ответ только с данными.
     *
     * @param data данные ответа
     * @param <T>  тип данных
     * @return успешный ApiResponse
     */
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .build();
    }

    /**
     * Создает ответ об ошибке.
     *
     * @param error сообщение об ошибке
     * @param <T>   тип данных
     * @return ApiResponse об ошибке
     */
    public static <T> ApiResponse<T> error(String error) {
        return ApiResponse.<T>builder()
                .success(false)
                .error(error)
                .build();
    }
}
