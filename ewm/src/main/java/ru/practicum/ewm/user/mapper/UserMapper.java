package ru.practicum.ewm.user.mapper;

import org.mapstruct.Mapper;
import ru.practicum.ewm.user.dto.ShortUserDto;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.entity.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toUser(UserDto userModelDto);

    UserDto toUserDto(User user);

    ShortUserDto toShortUserDto(User user);

    List<UserDto> toUserDtos(List<User> usersList);
}
