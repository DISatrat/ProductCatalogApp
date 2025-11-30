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
public class ProductUpdateDTO {
    private String name;
    private String category;
    private String brand;

    @Min(value = 0, message = "Price cannot be negative")
    private Double price;

    private String description;
}