package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.dto.BookingInfoInItem;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class ItemResponseForOwner {
    Long id;
    String name;
    String description;
    Boolean available;
    List<CommentResponseDto> comments;
    BookingInfoInItem lastBooking;
    BookingInfoInItem nextBooking;
}
