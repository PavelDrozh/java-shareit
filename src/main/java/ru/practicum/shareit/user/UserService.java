package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserCreatorDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.exceptions.ExistingEmailException;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@RequiredArgsConstructor
public class UserService {

    UserRepository repository;
    UserMapper mapper;

    public List<UserResponseDto> getAll() {
        return repository.getAll()
                .stream().map(mapper::userToUserResponseDto)
                .collect(Collectors.toList());
    }

    public UserResponseDto getById(long id) {
        User user = repository.getById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с таким id не найден"));
        return mapper.userToUserResponseDto(user);
    }

    public void getByEmail(String email) {
        repository.getByEmail(email)
                .ifPresent(u -> {
                    throw new ExistingEmailException("Пользователь с таким email уже существует");
                });
    }

    public UserResponseDto createUser(UserCreatorDto dto) {
        getByEmail(dto.getEmail());
        User user = mapper.userCreatorDtoToUser(dto);
        User created = repository.create(user);
        return mapper.userToUserResponseDto(created);
    }

    public UserResponseDto updateUser(UserUpdateDto dto, long id) {
        getById(id);
        getByEmail(dto.getEmail());
        User user = mapper.userUpdateDtoToUser(dto);
        user.setId(id);
        User updated = repository.update(user);
        return mapper.userToUserResponseDto(updated);
    }

    public UserResponseDto deleteUser(long id) {
        User deleted = repository.deleteById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с таким id не найден, удаление невозможно"));
        return mapper.userToUserResponseDto(deleted);
    }
}
