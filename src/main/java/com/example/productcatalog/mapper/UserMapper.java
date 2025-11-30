package com.example.productcatalog.mapper;

import com.example.productcatalog.dto.UserResponseDTO;
import com.example.productcatalog.model.User;
import com.example.productcatalog.model.enums.UserRole;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

/**
 * Маппер MapStruct для сущности User.
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    /**
     * Преобразует сущность User в UserResponseDTO.
     *
     * @param user сущность пользователя
     * @return DTO ответа пользователя
     */
    @Mapping(target = "userRole", source = "userRole", qualifiedByName = "roleToString")
    UserResponseDTO toDTO(User user);

    /**
     * Преобразует список сущностей User в список UserResponseDTO.
     *
     * @param users список сущностей пользователей
     * @return список DTO ответов пользователей
     */
    List<UserResponseDTO> toDTOList(List<User> users);

    /**
     * Преобразует enum UserRole в String.
     *
     * @param userRole enum роли пользователя
     * @return роль в виде строки
     */
    @Named("roleToString")
    static String roleToString(UserRole userRole) {
        return userRole != null ? userRole.name() : null;
    }
}
