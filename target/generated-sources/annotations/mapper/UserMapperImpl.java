package mapper;

import dto.UserResponseDTO;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import model.User;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-11-30T14:55:01+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.8 (Oracle Corporation)"
)
public class UserMapperImpl implements UserMapper {

    @Override
    public UserResponseDTO toDTO(User user) {
        if ( user == null ) {
            return null;
        }

        UserResponseDTO.UserResponseDTOBuilder userResponseDTO = UserResponseDTO.builder();

        userResponseDTO.userRole( UserMapper.userRoleToString( user.getUserRole() ) );
        userResponseDTO.id( user.getId() );
        userResponseDTO.username( user.getUsername() );
        userResponseDTO.createdAt( user.getCreatedAt() );

        return userResponseDTO.build();
    }

    @Override
    public List<UserResponseDTO> toDTOList(List<User> users) {
        if ( users == null ) {
            return null;
        }

        List<UserResponseDTO> list = new ArrayList<UserResponseDTO>( users.size() );
        for ( User user : users ) {
            list.add( toDTO( user ) );
        }

        return list;
    }
}
