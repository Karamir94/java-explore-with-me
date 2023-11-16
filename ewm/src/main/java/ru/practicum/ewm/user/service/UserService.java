package ru.practicum.ewm.user.service;

import ru.practicum.ewm.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto saveUser(UserDto userDto);

    List<UserDto> getUsers(List<Long> ids,
                           Integer from,
                           Integer size);

    void deleteUser(Long id);
}
