package ru.practicum.shareit.user.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;


@Data
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserCreatorDto {

    @NotBlank(message = "Имя не должно быть пустым")
    String name;

    @NotBlank(message = "Email не должен быть пустым")
    @Email(message = "Некорректный email")
    String email;

}
