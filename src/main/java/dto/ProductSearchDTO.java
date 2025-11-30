package dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Min;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class ProductSearchDTO {
    private String nameSubstr;
    private String category;
    private String brand;

    @Min(value = 0, message = "Minimum price cannot be negative")
    private Double priceMin;

    @Min(value = 0, message = "Maximum price cannot be negative")
    private Double priceMax;
}