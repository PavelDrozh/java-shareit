package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.supplier.ObjectSupplier;
import ru.practicum.shareit.user.dto.UserCreatorDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.exceptions.ExistingEmailException;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.mapper.UserMapperImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    UserService userService;
    UserRepository userRepository;
    UserMapperImpl mapper;

    User user;
    UserCreatorDto userCreatorDto;

    UserUpdateDto userUpdateDto;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        mapper = new UserMapperImpl();
        userService = new UserService(userRepository, mapper);
        user = ObjectSupplier.getDefaultUser();
        userCreatorDto = ObjectSupplier.getDefaultUserCreator();
        userUpdateDto = ObjectSupplier.getDefaultUserUpdate();
    }

    @Test
    void getAllTest() {
        when(userRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(user)));

        List<UserResponseDto> users = userService.getAll(0,10);

        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals(1, users.get(0).getId());
    }

    @Test
    void getByIdTest() {
        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(this.user));

        UserResponseDto result = userService.getById(user.getId());

        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
    }

    @Test
    void getByWrongIdTest() {
        when(userRepository.findById(any(Long.class)))
                .thenThrow(new UserNotFoundException(String.format("Пользователь с id = %d не найден",12)));

        assertThatThrownBy(() -> userService.getById(12))
                .isInstanceOf(UserNotFoundException.class)
                .message().isEqualTo(String.format("Пользователь с id = %d не найден",12));
    }

    @Test
    void createUserTest() {
        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        UserResponseDto result = userService.createUser(userCreatorDto);

        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
    }

    @Test
    void updateUserTest() {
        when(userRepository.getById(any(Long.class)))
                .thenReturn(user);
        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        UserResponseDto result = userService.updateUser(userUpdateDto, 1L);

        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        assertEquals(user.getName(), userUpdateDto.getName());
        assertEquals(user.getEmail(), userUpdateDto.getEmail());
    }

    @Test
    void deleteUserTest() {
        doNothing().when(userRepository).deleteById(any(Long.class));
        userService.deleteUser(user.getId());
        verify(userRepository, times(1)).deleteById(any(Long.class));
    }

    @Test
    void updateWrongEmailUserTest() {
        when(userRepository.getById(any(Long.class)))
                .thenReturn(user);
        when(userRepository.findByEmailContainingIgnoreCase(anyString()))
                .thenReturn(user);

        assertThatThrownBy(() -> userService.updateUser(userUpdateDto, 1L))
                .isInstanceOf(ExistingEmailException.class)
                .message().isEqualTo("Пользователь с таким email уже существует");
    }
}
