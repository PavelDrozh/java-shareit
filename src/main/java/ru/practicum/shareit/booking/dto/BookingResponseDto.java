package ru.practicum.shareit.booking.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.model.BookStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingResponseDto {
    Long id;
    LocalDateTime start;
    LocalDateTime end;
    BookStatus status;
    User booker;
    Item item;
}
