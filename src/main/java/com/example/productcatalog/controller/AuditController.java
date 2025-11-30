package com.example.productcatalog.controller;

import com.example.productcatalog.dto.ApiResponse;
import com.example.productcatalog.dto.audit.AuditEntryDTO;
import com.example.productcatalog.mapper.AuditMapper;
import com.example.productcatalog.model.AuditEntry;
import com.example.productcatalog.service.audit.AuditService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST контроллер для операций с журналом аудита.
 * <p>
 * Предоставляет конечные точки для получения журналов аудита.
 * Требуется роль ADMIN для доступа.
 * </p>
 */
@Slf4j
@Controller
@RequestMapping("/audit-logs")
@RequiredArgsConstructor
@Tag(name = "Audit", description = "Конечные точки журнала аудита (только для администраторов)")
public class AuditController {

    private final AuditService auditService;
    private final AuditMapper auditMapper;

    /**
     * Получает недавние записи аудита.
     * <p>
     * Эта конечная точка доступна только администраторам.
     * </p>
     *
     * @param limit    максимальное количество записей для получения
     * @param userRole роль запрашивающего пользователя
     * @return список недавних записей аудита
     */
    @GetMapping
    @Operation(summary = "Получить журналы аудита", description = "Получает недавние записи журнала аудита (только для администраторов)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Журналы аудита получены"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Доступ запрещен")
    })
    public ResponseEntity<ApiResponse<List<AuditEntryDTO>>> getAuditLogs(
            @Parameter(description = "Максимальное количество записей") @RequestParam(defaultValue = "50") int limit,
            @RequestHeader(value = "X-User-Role", defaultValue = "USER") String userRole) {
        log.debug("Получение журналов аудита, лимит: {}, запрошено ролью: {}", limit, userRole);

        if (!"ADMIN".equals(userRole)) {
            throw new SecurityException("Доступ запрещен. Требуется роль администратора.");
        }

        List<AuditEntry> entries = auditService.getRecentEntries(limit);
        List<AuditEntryDTO> dtos = auditMapper.toDTOList(entries);

        return ResponseEntity.ok(ApiResponse.success(dtos));
    }
}
