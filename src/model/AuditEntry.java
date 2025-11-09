package model;

import java.io.Serializable;
import java.util.Date;

public class AuditEntry implements Serializable {
    private static final long serialVersionUID = 1L;
    private Date timestamp;
    private String username;
    private String action;
    private String details;

    public AuditEntry(String username, String action, String details) {
        this.timestamp = new Date();
        this.username = username;
        this.action = action;
        this.details = details;
    }

    @Override
    public String toString() {
        return String.format("[%s] user=%s action=%s details=%s",
                timestamp.toString(), username, action, details);
    }
}
