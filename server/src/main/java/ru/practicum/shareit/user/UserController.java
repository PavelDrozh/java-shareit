package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserCreatorDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {

    private static final String ID_PATH = "/{id}";

    UserService service;

    @GetMapping
    public List<UserResponseDto> findAll(@RequestParam int from, @RequestParam int size) {
        return service.getAll(from, size);
    }

    @PostMapping
    public UserResponseDto create(@RequestBody UserCreatorDto dto) {
        return service.createUser(dto);
    }

    @PatchMapping(ID_PATH)
    public UserResponseDto update(@RequestBody UserUpdateDto dto, @PathVariable long id) {
        return service.updateUser(dto, id);
    }

    @GetMapping(ID_PATH)
    public UserResponseDto getUserById(@PathVariable long id) {
        return service.getById(id);
    }

    @DeleteMapping(ID_PATH)
    public void deleteUserById(@PathVariable long id) {
        service.deleteUser(id);
    }
}
