package mapper;

import dto.AuditEntryDTO;
import model.AuditEntry;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface AuditMapper {
    AuditMapper INSTANCE = Mappers.getMapper(AuditMapper.class);

    @Mapping(target = "timestamp", source = "timestamp", dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    AuditEntryDTO toDTO(AuditEntry auditEntry);
    List<AuditEntryDTO> toDTOList(List<AuditEntry> auditEntries);
}