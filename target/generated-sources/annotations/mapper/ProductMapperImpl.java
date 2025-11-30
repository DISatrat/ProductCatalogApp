package mapper;

import dto.ProductResponseDTO;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import model.Product;
import model.User;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-11-30T14:55:01+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.8 (Oracle Corporation)"
)
public class ProductMapperImpl implements ProductMapper {

    @Override
    public ProductResponseDTO toDTO(Product product) {
        if ( product == null ) {
            return null;
        }

        ProductResponseDTO.ProductResponseDTOBuilder productResponseDTO = ProductResponseDTO.builder();

        productResponseDTO.userId( productUserId( product ) );
        productResponseDTO.id( product.getId() );
        productResponseDTO.name( product.getName() );
        productResponseDTO.category( product.getCategory() );
        productResponseDTO.brand( product.getBrand() );
        productResponseDTO.price( product.getPrice() );
        productResponseDTO.description( product.getDescription() );
        productResponseDTO.createdAt( product.getCreatedAt() );

        return productResponseDTO.build();
    }

    @Override
    public List<ProductResponseDTO> toDTOList(List<Product> products) {
        if ( products == null ) {
            return null;
        }

        List<ProductResponseDTO> list = new ArrayList<ProductResponseDTO>( products.size() );
        for ( Product product : products ) {
            list.add( toDTO( product ) );
        }

        return list;
    }

    private Long productUserId(Product product) {
        if ( product == null ) {
            return null;
        }
        User user = product.getUser();
        if ( user == null ) {
            return null;
        }
        Long id = user.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
