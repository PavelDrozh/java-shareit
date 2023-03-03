package ru.practicum.shareit.request.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.item.dto.ItemResponseForItemRequest;

import java.time.LocalDateTime;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Data
public class ItemRequestResponseDto {
    Long id;
    String description;
    LocalDateTime created;
    List<ItemResponseForItemRequest> items;
}
