package ru.practicum.shareit.booking.mapper;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingInfoInItem;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.BookStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemInfoInBooking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookingMapperImpl implements BookingMapper {

    UserMapper userMapper;
    ItemMapper itemMapper;

    @Override
    public Booking creationBookingDtoToBooking(BookingCreationDto dto, Item item, User booker) {
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setEnd(dto.getEnd());
        booking.setStart(dto.getStart());
        booking.setStatus(BookStatus.WAITING);
        return booking;
    }

    @Override
    public BookingResponseDto bookingToBookingResponseDto(Booking booking) {
        UserResponseDto user = userMapper.userToUserResponseDto(booking.getBooker());
        ItemInfoInBooking item = itemMapper.itemToItemInfo(booking.getItem());
        return BookingResponseDto.builder()
                .id(booking.getId())
                .status(booking.getStatus())
                .start(booking.getStart())
                .end(booking.getEnd())
                .booker(user)
                .item(item)
                .build();
    }

    @Override
    public BookingInfoInItem bookingToBookingInfoInItem(Booking booking) {
        return BookingInfoInItem.builder()
                .id(booking.getId())
                .status(booking.getStatus())
                .bookerId(booking.getBooker().getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .build();
    }
}
