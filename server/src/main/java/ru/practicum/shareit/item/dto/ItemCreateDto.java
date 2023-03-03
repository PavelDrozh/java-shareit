package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

/**
 * TODO Sprint add-controllers.
 */

@Data
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Builder
public class ItemCreateDto {
    String name;
    String description;
    Boolean available;
    Long requestId;
}
