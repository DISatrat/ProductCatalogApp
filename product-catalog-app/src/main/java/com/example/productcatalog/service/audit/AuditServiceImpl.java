package com.example.productcatalog.service.audit;

import com.example.productcatalog.model.AuditEntry;
import com.example.productcatalog.repository.audit.AuditRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Реализация AuditService.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {

    private final AuditRepository auditRepository;

    @Override
    public void record(String username, String action, String details) {
        log.debug("Запись аудита: {} - {} - {}", username, action, details);

        AuditEntry entry = AuditEntry.builder()
                .timestamp(LocalDateTime.now())
                .username(username)
                .action(action)
                .details(details)
                .build();

        auditRepository.save(entry);
        log.info("Аудит записан: {} выполнил {}", username, action);
    }

    @Override
    public List<AuditEntry> getAllEntries() {
        log.debug("Получение всех записей аудита");
        return auditRepository.findAll();
    }

    @Override
    public List<AuditEntry> getRecentEntries(int limit) {
        log.debug("Получение {} недавних записей аудита", limit);
        return auditRepository.findRecent(limit);
    }
}
