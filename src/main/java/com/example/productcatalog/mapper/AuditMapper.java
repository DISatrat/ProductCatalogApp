package com.example.productcatalog.mapper;

import com.example.productcatalog.dto.audit.AuditEntryDTO;
import com.example.productcatalog.model.AuditEntry;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Маппер MapStruct для сущности AuditEntry.
 */
@Mapper(componentModel = "spring")
public interface AuditMapper {

    /**
     * Преобразует сущность AuditEntry в AuditEntryDTO.
     *
     * @param auditEntry сущность записи аудита
     * @return DTO записи аудита
     */
    @Mapping(target = "timestamp", source = "timestamp", qualifiedByName = "formatTimestamp")
    AuditEntryDTO toDTO(AuditEntry auditEntry);

    /**
     * Преобразует список сущностей AuditEntry в список AuditEntryDTO.
     *
     * @param auditEntries список сущностей записей аудита
     * @return список DTO записей аудита
     */
    List<AuditEntryDTO> toDTOList(List<AuditEntry> auditEntries);

    /**
     * Форматирует LocalDateTime в ISO строку.
     *
     * @param timestamp временная метка
     * @return отформатированная строка временной метки
     */
    @Named("formatTimestamp")
    static String formatTimestamp(LocalDateTime timestamp) {
        return timestamp != null
                ? timestamp.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                : null;
    }
}
