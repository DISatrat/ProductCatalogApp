package service.audit;

import model.AuditEntry;
import repository.audit.AuditRepository;

import java.util.List;

/**
 * Реализация сервиса аудита
 * Использует AuditRepository для сохранения и извлечения записей аудита.
 */
public class AuditServiceImpl implements AuditService {

    private final AuditRepository auditRepository;

    public AuditServiceImpl(AuditRepository auditRepository) {
        this.auditRepository = auditRepository;
    }

    @Override
    public void record(String username, String action, String details) {
        if (username == null || action == null || details == null) {
            throw new NullPointerException("Username, action and details cannot be null");
        }

        auditRepository.record(username, action, details);
    }

    @Override
    public List<AuditEntry> getEntries() {
        return auditRepository.getEntries();
    }

}