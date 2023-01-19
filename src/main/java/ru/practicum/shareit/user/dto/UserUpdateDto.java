package ru.practicum.shareit.user.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Email;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserUpdateDto {

    String name;

    @Email(message = "Некорректный email")
    String email;
}

