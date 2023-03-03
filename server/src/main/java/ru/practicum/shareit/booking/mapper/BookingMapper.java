package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingInfoInItem;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public interface BookingMapper {

    Booking creationBookingDtoToBooking(BookingCreationDto dto, Item item, User booker);

    BookingResponseDto bookingToBookingResponseDto(Booking booking);

    BookingInfoInItem bookingToBookingInfoInItem(Booking booking);

}
