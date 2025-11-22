package test.main.service;

import model.AuditEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.audit.AuditRepository;
import service.audit.AuditService;
import service.audit.AuditServiceImpl;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditServiceTest {

    @Mock
    private AuditRepository auditRepository;

    private AuditService auditService;

    @BeforeEach
    void setUp() {
        auditService = new AuditServiceImpl(auditRepository);
    }

    @Test
    void testRecord() {
        String username = "testuser";
        String action = "LOGIN";
        String details = "User logged into the system";

        auditService.record(username, action, details);

        verify(auditRepository, times(1)).record(username, action, details);
    }

    @Test
    void testRecordWithNullUsername() {
        String username = null;
        String action = "ACTION";
        String details = "Details";

        assertThrows(IllegalArgumentException.class, () -> {
            auditService.record(username, action, details);
        });

        verify(auditRepository, never()).record(any(), any(), any());
    }

    @Test
    void testRecordWithNullAction() {
        String username = "user";
        String action = null;
        String details = "Details";

        assertThrows(IllegalArgumentException.class, () -> {
            auditService.record(username, action, details);
        });

        verify(auditRepository, never()).record(any(), any(), any());
    }

    @Test
    void testRecordWithNullDetails() {
        String username = "user";
        String action = "ACTION";
        String details = null;

        assertThrows(IllegalArgumentException.class, () -> {
            auditService.record(username, action, details);
        });

        verify(auditRepository, never()).record(any(), any(), any());
    }

    @Test
    void testGetEntries() {
        List<AuditEntry> expectedEntries = Arrays.asList(
                createAuditEntry(1L, "user1", "LOGIN", "User logged in"),
                createAuditEntry(2L, "user2", "CREATE_PRODUCT", "Created product")
        );

        when(auditRepository.getEntries()).thenReturn(expectedEntries);

        List<AuditEntry> actualEntries = auditService.getEntries();

        assertNotNull(actualEntries);
        assertEquals(2, actualEntries.size());
        assertEquals(expectedEntries, actualEntries);

        verify(auditRepository, times(1)).getEntries();
    }

    @Test
    void testGetEntriesEmpty() {
        when(auditRepository.getEntries()).thenReturn(List.of());

        List<AuditEntry> entries = auditService.getEntries();

        assertNotNull(entries);
        assertTrue(entries.isEmpty());
        verify(auditRepository, times(1)).getEntries();
    }

    private AuditEntry createAuditEntry(Long id, String username, String action, String details) {
        AuditEntry entry = new AuditEntry();
        entry.setId(id);
        entry.setUsername(username);
        entry.setAction(action);
        entry.setDetails(details);
        return entry;
    }
}
