package repository.audit;

import exception.AuditRepositoryException;
import model.AuditEntry;
import util.ConnectionPoolManager;
import util.SQLConstants;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.ArrayList;

/**
 * Реализация репозитория аудита
 */
public class AuditRepositoryImpl implements AuditRepository {

    public AuditRepositoryImpl() {
    }

    @Override
    public void record(String username, String action, String details) {
        try (Connection connection = ConnectionPoolManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(SQLConstants.Audit.INSERT)) {

            stmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            stmt.setString(2, username);
            stmt.setString(3, action);
            stmt.setString(4, details);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new AuditRepositoryException("Failed to record audit entry, no rows affected");
            }

        } catch (SQLException e) {
            throw new AuditRepositoryException(
                    String.format("Database error while recording audit entry [user: %s, action: %s]", username, action), e);
        }
    }

    @Override
    public List<AuditEntry> getEntries() {
        List<AuditEntry> entries = new ArrayList<>();

        try (Connection connection = ConnectionPoolManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(SQLConstants.Audit.SELECT_ALL);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                AuditEntry entry = new AuditEntry();
                entry.setId(rs.getLong("id"));
                entry.setTimestamp(rs.getTimestamp("timestamp"));
                entry.setUsername(rs.getString("username"));
                entry.setAction(rs.getString("action"));
                entry.setDetails(rs.getString("details"));
                entries.add(entry);
            }

        } catch (SQLException e) {
            throw new AuditRepositoryException("Database error while retrieving audit entries", e);
        }

        return entries;
    }
}