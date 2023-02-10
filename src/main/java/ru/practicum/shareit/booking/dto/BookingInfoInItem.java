package ru.practicum.shareit.booking.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.model.BookStatus;

import java.time.LocalDateTime;

@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingInfoInItem {
    Long id;
    LocalDateTime start;
    LocalDateTime end;
    BookStatus status;
    Long bookerId;
}
