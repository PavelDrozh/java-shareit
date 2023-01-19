package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import ru.practicum.shareit.user.dto.UserCreatorDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
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

    private final static String ID_PATH = "/{id}";

    UserService service;

    @GetMapping
    public List<UserResponseDto> findAll() {
        return service.getAll();
    }

    @PostMapping
    public UserResponseDto create(@RequestBody @NotNull @Valid UserCreatorDto dto) {
        return service.createUser(dto);
    }

    @PatchMapping(ID_PATH)
    public UserResponseDto update(@RequestBody @NotNull @Valid UserUpdateDto dto, @PathVariable long id) {
        return service.updateUser(dto, id);
    }

    @GetMapping(ID_PATH)
    public UserResponseDto getUserById(@PathVariable long id) {
        return service.getById(id);
    }

    @DeleteMapping(ID_PATH)
    public UserResponseDto deleteUserById(@PathVariable long id) {
        return service.deleteUser(id);
    }
}
