package ru.practicum.shareit.request.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;

/**
 * TODO Sprint add-item-requests.
 */

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequestCreatorDto {

    @NotBlank
    String description;
}
