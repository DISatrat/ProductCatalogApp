package main.service.audit;

import main.model.AuditEntry;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Реализация сервиса аудита с хранением записей в памяти и дублированием в файл.
 * Обеспечивает постоянное хранение записей аудита в файле audit.log.
 */
public class AuditServiceImpl implements AuditService {
    /** Внутреннее хранилище записей аудита в памяти */
    private final List<AuditEntry> entries = new ArrayList<>();

    /** Файл для постоянного хранения записей аудита */
    private final File auditFile = new File("./audit.log");


    @Override
    public void record(String username, String action, String details) {
        if (username == null || action == null || details == null) {
            throw new NullPointerException("Username, action and details cannot be null");
        }

        AuditEntry entry = new AuditEntry(username, action, details);
        entries.add(entry);
        appendToFile(entry);
    }

    @Override
    public void appendToFile(AuditEntry entry) {
        if (entry == null) {
            throw new NullPointerException("Audit entry cannot be null");
        }

        try (FileWriter fileWriter = new FileWriter(auditFile, true);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
             PrintWriter printWriter = new PrintWriter(bufferedWriter)) {
            printWriter.println(entry.toString());
        } catch (IOException exception) {
            System.err.println("Не удалось записать audit: " + exception.getMessage());
        }
    }

    @Override
    public List<AuditEntry> getEntries() {
        return new ArrayList<>(entries);
    }
}
