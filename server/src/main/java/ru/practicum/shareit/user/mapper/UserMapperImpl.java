package ru.practicum.shareit.user.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserCreatorDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

@Component
public class UserMapperImpl implements UserMapper {
    @Override
    public User userCreatorDtoToUser(UserCreatorDto dto) {
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setName(dto.getName());
        return user;
    }

    @Override
    public User userUpdateDtoToUser(UserUpdateDto dto) {
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setName(dto.getName());
        return user;
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
