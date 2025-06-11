package com.alkemy.java2.TPIntegrador.mappers;

import com.alkemy.java2.TPIntegrador.DTOs.UserDTO;
import com.alkemy.java2.TPIntegrador.DTOs.UserLogInDTO;
import com.alkemy.java2.TPIntegrador.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDTO toDTO(User user);
    User toEntity(UserDTO dto);

    //UserLogInDTO
     UserLogInDTO toLogInDTO(User user);
    User toEntity(UserLogInDTO dto);
}