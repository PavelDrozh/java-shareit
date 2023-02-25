package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class ItemResponseForItemRequest {
    Long id;
    Long ownerId;
    Long requestId;
    String name;
    String description;
    boolean available;
}
