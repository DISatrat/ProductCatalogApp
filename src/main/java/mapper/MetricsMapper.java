package mapper;

import dto.MetricsResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MetricsMapper {
    MetricsMapper INSTANCE = Mappers.getMapper(MetricsMapper.class);

    default MetricsResponseDTO toDTO(long searchCount, double averageSearchTimeMs) {
        return new MetricsResponseDTO(searchCount, averageSearchTimeMs);
    }
}