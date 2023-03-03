package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserCreatorDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Validated
public class UserController {

    private static final String ID_PATH = "/{id}";

    UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getAll(@PositiveOrZero @RequestParam(required = false, defaultValue = "0") int from,
                                         @Positive @RequestParam(required = false, defaultValue = "10") int size) {
        return userClient.getAll(from, size);
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody @NotNull @Valid UserCreatorDto dto) {
        return userClient.createUser(dto);
    }

    @PatchMapping(ID_PATH)
    public ResponseEntity<Object> update(@RequestBody @NotNull @Valid UserUpdateDto dto, @PathVariable long id) {
        return userClient.updateUser(dto, id);
    }

    @GetMapping(ID_PATH)
    public ResponseEntity<Object> getUserById(@PathVariable long id) {
        return userClient.getById(id);
    }

    @DeleteMapping(ID_PATH)
    public ResponseEntity<Object> deleteUserById(@PathVariable long id) {
        return userClient.deleteUser(id);
    }
}
