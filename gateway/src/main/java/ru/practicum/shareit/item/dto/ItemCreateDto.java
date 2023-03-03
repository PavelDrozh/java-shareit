package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */

@Data
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Builder
public class ItemCreateDto {
    @NotBlank(message = "Отсутствует наименование вещи")
    String name;
    @NotBlank(message = "Отсутствует описание вещи")
    String description;
    @NotNull(message = "Отсутствует статус вещи")
    Boolean available;
    Long requestId;
}
