package service;

import model.AuditEntry;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AuditService {
    private final List<AuditEntry> entries = new ArrayList<>();
    private final File auditFile = new File("audit.log");

    public AuditService() {
    }

    public synchronized void record(String username, String action, String details) {
        AuditEntry e = new AuditEntry(username, action, details);
        entries.add(e);
        appendToFile(e);
    }

    private void appendToFile(AuditEntry e) {
        try (FileWriter fw = new FileWriter(auditFile, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.println(e.toString());
        } catch (IOException ex) {
            System.err.println("Не удалось записать audit: " + ex.getMessage());
        }
    }

    public synchronized List<AuditEntry> getEntries() {
        return new ArrayList<>(entries);
    }
}
