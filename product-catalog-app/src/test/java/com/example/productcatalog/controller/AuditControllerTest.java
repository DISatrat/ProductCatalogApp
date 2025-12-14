package com.example.productcatalog.controller;

import com.example.productcatalog.dto.audit.AuditEntryDTO;
import com.example.productcatalog.model.AuditEntry;
import com.example.productcatalog.mapper.AuditMapper;
import com.example.productcatalog.service.audit.AuditService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Интеграционные тесты для AuditController
 */
@SpringBootTest
@AutoConfigureMockMvc
class AuditControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuditService auditService;

    @MockBean
    private AuditMapper auditMapper;

    @Test
    @DisplayName("Должен вернуть журналы аудита для администратора")
    void getAuditLogs_ShouldReturnLogs_WhenAdmin() throws Exception {
        List<AuditEntry> entries = Arrays.asList(
                AuditEntry.builder()
                        .id(1L)
                        .username("user1")
                        .action("CREATE")
                        .timestamp(LocalDateTime.now())
                        .build(),
                AuditEntry.builder()
                        .id(2L)
                        .username("user2")
                        .action("UPDATE")
                        .timestamp(LocalDateTime.now())
                        .build()
        );

        List<AuditEntryDTO> dtos = Arrays.asList(
                AuditEntryDTO.builder()
                        .id(1L)
                        .username("user1")
                        .action("CREATE")
                        .build(),
                AuditEntryDTO.builder()
                        .id(2L)
                        .username("user2")
                        .action("UPDATE")
                        .build()
        );

        when(auditService.getAllEntries()).thenReturn(entries);
        when(auditMapper.toDTOList(entries)).thenReturn(dtos);

        mockMvc.perform(get("/audit-logs")
                        .header("X-User-Role", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    @DisplayName("Должен вернуть ошибку 403 для обычного пользователя")
    void getAuditLogs_ShouldReturn403_WhenNotAdmin() throws Exception {
        mockMvc.perform(get("/audit-logs")
                        .header("X-User-Role", "USER"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Должен вернуть ошибку 403 когда отсутствует заголовок роли")
    void getAuditLogs_ShouldReturn403_WhenNoRoleHeader() throws Exception {
        mockMvc.perform(get("/audit-logs"))
                .andExpect(status().isForbidden());
    }
}