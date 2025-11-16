package repository.audit;

import model.AuditEntry;

import java.sql.Connection;
import java.util.List;
import java.sql.*;
import java.util.ArrayList;

/**
 * Реализация репозитория аудита
 */
public class AuditRepositoryImpl implements AuditRepository {

    private final Connection connection;

    public AuditRepositoryImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void record(String username, String action, String details) {
        String sql = "INSERT INTO app_schema.audit_entries (timestamp, username, action, details) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            stmt.setString(2, username);
            stmt.setString(3, action);
            stmt.setString(4, details);

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Database error while recording audit entry", e);
        }
    }

    @Override
    public List<AuditEntry> getEntries() {
        List<AuditEntry> entries = new ArrayList<>();
        String sql = "SELECT id, timestamp, username, action, details FROM app_schema.audit_entries ORDER BY timestamp DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
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
            throw new RuntimeException("Database error while retrieving audit entries", e);
        }

        return entries;
    }
}
