package ru.practicum.shareit.user.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserCreatorDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

@Component
public class UserMapperImpl implements UserMapper {
    @Override
    public User userCreatorDtoToUser(UserCreatorDto dto) {
        return User.builder()
                .email(dto.getEmail())
                .name(dto.getName())
                .build();
    }

    @Override
    public User userUpdateDtoToUser(UserUpdateDto dto) {
        return User.builder()
                .email(dto.getEmail())
                .name(dto.getName())
                .build();
    }

    @Override
    public UserResponseDto userToUserResponseDto(User source) {
        return UserResponseDto.builder()
                .email(source.getEmail())
                .name(source.getName())
                .id(source.getId())
                .build();
    }
}
