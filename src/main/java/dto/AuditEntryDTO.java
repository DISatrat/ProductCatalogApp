package dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditEntryDTO {
    private Long id;
    private String timestamp;
    private String username;
    private String action;
    private String details;
}