package mapper;

import dto.UserResponseDTO;
import model.User;
import model.enums.UserRole;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "userRole", source = "userRole", qualifiedByName = "userRoleToString")
    UserResponseDTO toDTO(User user);

    List<UserResponseDTO> toDTOList(List<User> users);


    @Named("userRoleToString")
    static String userRoleToString(UserRole userRole) {
        return userRole != null ? userRole.name() : null;
    }
}