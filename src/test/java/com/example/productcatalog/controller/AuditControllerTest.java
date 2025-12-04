package com.example.productcatalog.controller;

import com.example.productcatalog.dto.audit.AuditEntryDTO;
import com.example.productcatalog.model.AuditEntry;
import com.example.productcatalog.mapper.AuditMapper;
import com.example.productcatalog.service.audit.AuditService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * WebMVC тесты для AuditController.
 */
@ExtendWith(MockitoExtension.class)
class AuditControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuditService auditService;

    @Mock
    private AuditMapper auditMapper;

    @InjectMocks
    private AuditController auditController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(auditController).build();
    }

    @Test
    @DisplayName("Должен вернуть журналы аудита для администратора")
    void getAuditLogs_ShouldReturnLogs_WhenAdmin() throws Exception {
        List<AuditEntry> entries = Arrays.asList(
                AuditEntry.builder()
                        .id(1L)
                        .timestamp(LocalDateTime.now())
                        .username("user1")
                        .action("LOGIN")
                        .details("Пользователь вошел")
                        .build(),
                AuditEntry.builder()
                        .id(2L)
                        .timestamp(LocalDateTime.now())
                        .username("user2")
                        .action("LOGOUT")
                        .details("Пользователь вышел")
                        .build()
        );

        List<AuditEntryDTO> dtos = Arrays.asList(
                AuditEntryDTO.builder()
                        .id(1L)
                        .timestamp("2024-01-15T10:30:00")
                        .username("user1")
                        .action("LOGIN")
                        .details("Пользователь вошел")
                        .build(),
                AuditEntryDTO.builder()
                        .id(2L)
                        .timestamp("2024-01-15T10:35:00")
                        .username("user2")
                        .action("LOGOUT")
                        .details("Пользователь вышел")
                        .build()
        );

        when(auditService.getRecentEntries(50)).thenReturn(entries);
        when(auditMapper.toDTOList(entries)).thenReturn(dtos);

        mockMvc.perform(get("/audit-logs")
                        .header("X-User-Role", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    @DisplayName("Должен вернуть журналы аудита с пользовательским лимитом")
    void getAuditLogs_ShouldRespectLimit() throws Exception {
        List<AuditEntry> entries = Arrays.asList(
                AuditEntry.builder().id(1L).timestamp(LocalDateTime.now())
                        .username("user1").action("LOGIN").details("Подробности").build()
        );

        List<AuditEntryDTO> dtos = Arrays.asList(
                AuditEntryDTO.builder().id(1L).username("user1").action("LOGIN").build()
        );

        when(auditService.getRecentEntries(10)).thenReturn(entries);
        when(auditMapper.toDTOList(entries)).thenReturn(dtos);

        mockMvc.perform(get("/audit-logs")
                        .param("limit", "10")
                        .header("X-User-Role", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Должен вернуть ошибку 403 для обычных пользователей")
    void getAuditLogs_ShouldReturn403_WhenNotAdmin() throws Exception {
        mockMvc.perform(get("/audit-logs")
                        .header("X-User-Role", "USER"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Должен вернуть ошибку 403, когда отсутствует заголовок роли")
    void getAuditLogs_ShouldReturn403_WhenNoRoleHeader() throws Exception {
        mockMvc.perform(get("/audit-logs"))
                .andExpect(status().isForbidden());
    }
}