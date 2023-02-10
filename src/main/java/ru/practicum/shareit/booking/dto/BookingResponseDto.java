package ru.practicum.shareit.booking.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.model.BookStatus;
import ru.practicum.shareit.item.dto.ItemInfoInBooking;
import ru.practicum.shareit.user.dto.UserResponseDto;

import java.time.LocalDateTime;

@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingResponseDto {
    Long id;
    LocalDateTime start;
    LocalDateTime end;
    BookStatus status;
    UserResponseDto booker;
    ItemInfoInBooking item;
}
