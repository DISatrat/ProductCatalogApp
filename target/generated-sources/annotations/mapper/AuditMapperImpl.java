package mapper;

import dto.AuditEntryDTO;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import model.AuditEntry;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-11-30T14:55:01+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.8 (Oracle Corporation)"
)
public class AuditMapperImpl implements AuditMapper {

    @Override
    public AuditEntryDTO toDTO(AuditEntry auditEntry) {
        if ( auditEntry == null ) {
            return null;
        }

        AuditEntryDTO.AuditEntryDTOBuilder auditEntryDTO = AuditEntryDTO.builder();

        if ( auditEntry.getTimestamp() != null ) {
            auditEntryDTO.timestamp( new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'" ).format( auditEntry.getTimestamp() ) );
        }
        auditEntryDTO.id( auditEntry.getId() );
        auditEntryDTO.username( auditEntry.getUsername() );
        auditEntryDTO.action( auditEntry.getAction() );
        auditEntryDTO.details( auditEntry.getDetails() );

        return auditEntryDTO.build();
    }

    @Override
    public List<AuditEntryDTO> toDTOList(List<AuditEntry> auditEntries) {
        if ( auditEntries == null ) {
            return null;
        }

        List<AuditEntryDTO> list = new ArrayList<AuditEntryDTO>( auditEntries.size() );
        for ( AuditEntry auditEntry : auditEntries ) {
            list.add( toDTO( auditEntry ) );
        }

        return list;
    }
}
