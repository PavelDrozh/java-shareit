package ru.practicum.shareit.booking.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingCreationDto {
    @NotNull
    Long itemId;

    @NotNull
    LocalDateTime start;

    @NotNull
    LocalDateTime end;
}