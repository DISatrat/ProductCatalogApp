package dto;

import lombok.*;

@Builder
@NoArgsConstructor
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class AuditEntryDTO {
    private Long id;
    private String timestamp;
    private String username;
    private String action;
    private String details;

}