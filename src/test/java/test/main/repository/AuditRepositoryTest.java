package test.main.repository;

import exception.AuditRepositoryException;
import model.AuditEntry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.audit.AuditRepository;
import repository.audit.AuditRepositoryImpl;
import util.SQLConstants;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditRepositoryTest {

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    private AuditRepository auditRepository;

    @Test
    void testRecord_ShouldExecuteInsertStatement() throws SQLException {
        auditRepository = new AuditRepositoryImpl();
        when(connection.prepareStatement(SQLConstants.Audit.INSERT)).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        String username = "testuser";
        String action = "LOGIN";
        String details = "User logged in";

        auditRepository.record(username, action, details);

        verify(connection).prepareStatement(SQLConstants.Audit.INSERT);
        verify(preparedStatement).setTimestamp(1, any(Timestamp.class));
        verify(preparedStatement).setString(2, username);
        verify(preparedStatement).setString(3, action);
        verify(preparedStatement).setString(4, details);
        verify(preparedStatement).executeUpdate();
    }

    @Test
    void testGetEntries_ShouldReturnListOfEntries() throws SQLException {
        auditRepository = new AuditRepositoryImpl();
        when(connection.prepareStatement(SQLConstants.Audit.SELECT_ALL)).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, true, false);
        when(resultSet.getLong("id")).thenReturn(1L, 2L);
        when(resultSet.getTimestamp("timestamp")).thenReturn(new Timestamp(System.currentTimeMillis()));
        when(resultSet.getString("username")).thenReturn("user1", "user2");
        when(resultSet.getString("action")).thenReturn("LOGIN", "CREATE");
        when(resultSet.getString("details")).thenReturn("Details1", "Details2");

        List<AuditEntry> entries = auditRepository.getEntries();

        assertEquals(2, entries.size());
        verify(connection).prepareStatement(SQLConstants.Audit.SELECT_ALL);
        verify(preparedStatement).executeQuery();
        verify(resultSet, times(3)).next();
    }

    @Test
    void testRecord_ShouldThrowExceptionOnSQLException() throws SQLException {
        auditRepository = new AuditRepositoryImpl();
        when(connection.prepareStatement(SQLConstants.Audit.INSERT)).thenThrow(new SQLException("DB error"));

        assertThrows(AuditRepositoryException.class, () -> {
            auditRepository.record("user", "action", "details");
        });
    }
}