package dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class ProductResponseDTO {
    private Long id;
    private String name;
    private String category;
    private String brand;
    private double price;
    private String description;
    private Long userId;
    private Date createdAt;
}