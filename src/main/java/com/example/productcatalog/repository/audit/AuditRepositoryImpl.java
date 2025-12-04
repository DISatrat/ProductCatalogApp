package com.example.productcatalog.repository.audit;

import com.example.productcatalog.exception.RepositoryException;
import com.example.productcatalog.model.AuditEntry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.List;

/**
 * Реализация AuditRepository с использованием JDBC.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuditRepositoryImpl implements AuditRepository {

    private final JdbcTemplate jdbcTemplate;

    private static final String INSERT =
            "INSERT INTO app_schema.audit_entries (timestamp, username, action, details) VALUES (?, ?, ?, ?)";

    private static final String SELECT_ALL =
            "SELECT id, timestamp, username, action, details FROM app_schema.audit_entries ORDER BY timestamp DESC";

    private static final String SELECT_RECENT =
            "SELECT id, timestamp, username, action, details FROM app_schema.audit_entries ORDER BY timestamp DESC LIMIT ?";

    private final RowMapper<AuditEntry> auditRowMapper = (rs, rowNum) -> AuditEntry.builder()
            .id(rs.getLong("id"))
            .timestamp(rs.getTimestamp("timestamp").toLocalDateTime())
            .username(rs.getString("username"))
            .action(rs.getString("action"))
            .details(rs.getString("details"))
            .build();

    @Override
    public void save(AuditEntry entry) {
        log.debug("Запись аудита: {} - {}", entry.getUsername(), entry.getAction());
        try {
            jdbcTemplate.update(INSERT,
                    Timestamp.valueOf(entry.getTimestamp()),
                    entry.getUsername(),
                    entry.getAction(),
                    entry.getDetails());
            log.info("Аудит записан: {} выполнил {}", entry.getUsername(), entry.getAction());
        } catch (Exception e) {
            log.error("Ошибка записи аудита", e);
            throw new RepositoryException("Ошибка записи аудита", e);
        }
    }

    @Override
    public List<AuditEntry> findAll() {
        log.debug("Поиск всех записей аудита");
        try {
            return jdbcTemplate.query(SELECT_ALL, auditRowMapper);
        } catch (Exception e) {
            log.error("Ошибка поиска всех записей аудита", e);
            throw new RepositoryException("Ошибка поиска всех записей аудита", e);
        }
    }

    @Override
    public List<AuditEntry> findRecent(int limit) {
        log.debug("Поиск {} недавних записей аудита", limit);
        try {
            return jdbcTemplate.query(SELECT_RECENT, auditRowMapper, limit);
        } catch (Exception e) {
            log.error("Ошибка поиска недавних записей аудита", e);
            throw new RepositoryException("Ошибка поиска недавних записей аудита", e);
        }
    }
}
