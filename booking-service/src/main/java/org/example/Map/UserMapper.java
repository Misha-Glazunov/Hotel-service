package org.example.Map;

import org.example.DTO.UserDTO;
import org.example.Entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
    UserDTO toUserDTO(User user);
    List<UserDTO> toUserDTOList(List<User> users);
}
