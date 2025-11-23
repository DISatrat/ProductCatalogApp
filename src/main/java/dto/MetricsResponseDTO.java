package dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class MetricsResponseDTO {
    private long searchCount;
    private double averageSearchTimeMs;
}