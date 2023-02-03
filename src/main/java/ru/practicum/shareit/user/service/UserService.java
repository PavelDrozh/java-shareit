package ru.practicum.shareit.user.service;

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
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@RequiredArgsConstructor
public class UserService {

    UserRepository repository;
    UserMapper mapper;

    public List<UserResponseDto> getAll() {
        return repository.findAll()
                .stream().map(mapper::userToUserResponseDto)
                .collect(Collectors.toList());
    }

    public UserResponseDto getById(long id) {
        User result = getUser(id);
        return mapper.userToUserResponseDto(result);
    }

    public User getUser(long id) {
        Optional<User> user = repository.findById(id);
        if (user.isEmpty()) {
            throw new UserNotFoundException("Пользователь с таким id не найден");
        }
        return user.get();
    }

    public void getByEmail(String email) {
        User user = repository.findByEmailContainingIgnoreCase(email);
        if (user != null) {
            throw new ExistingEmailException("Пользователь с таким email уже существует");
        }
    }

    public UserResponseDto createUser(UserCreatorDto dto) {
        User user = mapper.userCreatorDtoToUser(dto);
        User created = repository.save(user);
        return mapper.userToUserResponseDto(created);
    }

    public UserResponseDto updateUser(UserUpdateDto dto, long id) {
        User user = repository.getById(id);
        if (dto.getEmail() != null) {
            getByEmail(dto.getEmail());
            user.setEmail(dto.getEmail());
        }
        if (dto.getName() != null) {
            user.setName(dto.getName());
        }
        User updated = repository.save(user);
        return mapper.userToUserResponseDto(updated);
    }

    public void deleteUser(long id) {
        repository.deleteById(id);
    }
}
