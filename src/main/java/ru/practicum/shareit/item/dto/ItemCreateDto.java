package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */

@Data
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Builder
public class ItemCreateDto {
    @NotNull
    @NotBlank
    @NotEmpty
    String name;
    @NotNull
    @NotBlank
    @NotEmpty
    String description;
    @NotNull
    Boolean available;
}
