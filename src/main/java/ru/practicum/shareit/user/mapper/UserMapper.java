package ru.practicum.shareit.user.mapper;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserCreatorDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

public interface UserMapper {
    User userCreatorDtoToUser(UserCreatorDto dto);
    User userUpdateDtoToUser(UserUpdateDto dto);
    UserResponseDto userToUserResponseDto(User source);
}
