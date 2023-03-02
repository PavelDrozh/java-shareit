package ru.practicum.shareit.supplier;

import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.BookStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemInfoInBooking;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemResponseForOwner;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestCreatorDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserCreatorDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ObjectSupplier {

    public static User getDefaultUser() {
        User user = new User();
        user.setId(1L);
        user.setName("User");
        user.setEmail("email@yandex.ru");
        return user;
    }

    public static UserCreatorDto getDefaultUserCreator() {
        UserCreatorDto userCreatorDto = new UserCreatorDto();
        userCreatorDto.setName("UserName");
        userCreatorDto.setEmail("email@yandex.ru");
        return userCreatorDto;
    }

    public static UserUpdateDto getDefaultUserUpdate() {
        UserUpdateDto userUpdateDto = new UserUpdateDto();
        userUpdateDto.setName("UpdatedName");
        userUpdateDto.setEmail("updatedEmail@yandex.ru");
        return userUpdateDto;
    }

    public static Item getDefaultItem() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Item Name");
        item.setOwner(1L);
        item.setDescription("Item Description");
        item.setAvailable(true);
        item.setRequest(getDefaultItemRequest());
        item.setComments(new ArrayList<>());
        return item;
    }

    public static Booking getDefaultBooking() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setItem(getDefaultItem());
        booking.setStatus(BookStatus.WAITING);
        booking.setBooker(getDefaultUser());
        booking.setStart(getDefaultBookingCreateDto().getStart());
        booking.setEnd(getDefaultBookingCreateDto().getEnd());
        return booking;
    }

    public static ItemRequest getDefaultItemRequest() {
        User user = getDefaultUser();
        user.setId(2L);
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setCreator(user);
        itemRequest.setCreated(LocalDateTime.of(2023, 2,19,14,37, 20));
        itemRequest.setDescription(getDefaultItemRequestCreateDto().getDescription());
        itemRequest.setId(1L);
        return itemRequest;
    }

    public static ItemCreateDto getDefaultItemCreateDto() {
        return ItemCreateDto.builder()
                .name(getDefaultItem().getName())
                .description(getDefaultItem().getDescription())
                .requestId(1L)
                .available(getDefaultItem().getAvailable())
                .build();
    }

    public static BookingCreationDto getDefaultBookingCreateDto() {
        BookingCreationDto bookingCreationDto = new BookingCreationDto();
        bookingCreationDto.setItemId(1L);
        bookingCreationDto.setStart(LocalDateTime.of(2023, 3,19,14,37, 20));
        bookingCreationDto.setEnd(LocalDateTime.of(2023, 3,20,14,37, 20));
        return bookingCreationDto;
    }

    public static ItemRequestCreatorDto getDefaultItemRequestCreateDto() {
        ItemRequestCreatorDto itemRequestCreatorDto = new ItemRequestCreatorDto();
        itemRequestCreatorDto.setDescription("Request description");
        return itemRequestCreatorDto;
    }

    public static ItemUpdateDto getDefaultItemUpdateDto() {
        return ItemUpdateDto.builder()
                .name("Updated Name")
                .description("Updated Description")
                .available(false)
                .build();
    }

    public static Comment getDefaultComment() {
        Comment comment = new Comment();
        comment.setCreated(LocalDateTime.of(2023, 2,12,14,37, 20));
        comment.setText("comment");
        comment.setItem(getDefaultItem());
        comment.setAuthor(getDefaultUser());
        comment.setId(1L);
        return comment;
    }

    public static UserResponseDto getDefaultUserResponse() {
        return UserResponseDto.builder()
                .id(1L)
                .name("UserName")
                .email("email@yandex.ru")
                .build();
    }

    public static ItemInfoInBooking getDefaultItemInfo() {
        return ItemInfoInBooking.builder()
                .id(1L)
                .description("Item description")
                .available(true)
                .name("Item")
                .build();
    }

    public static BookingResponseDto getDefaultBookingResponseDto() {
        return BookingResponseDto.builder()
                .id(1L)
                .end(getDefaultBookingCreateDto().getEnd())
                .start(getDefaultBookingCreateDto().getStart())
                .booker(getDefaultUserResponse())
                .item(getDefaultItemInfo())
                .status(BookStatus.WAITING)
                .build();
    }

    public static ItemResponseDto getDefaultItemResponseDto() {
        return ItemResponseDto.builder()
                .id(1L)
                .description("Item description")
                .available(true)
                .name("Item")
                .build();
    }

    public static ItemResponseDto getUpdatedItemResponseDto() {
        return ItemResponseDto.builder()
                .id(1L)
                .name("ItemUpdate")
                .description("Item update description")
                .available(false)
                .build();
    }

    public static CommentResponseDto getCommentResponseDto() {
        return CommentResponseDto.builder()
                .id(1L)
                .text("comment")
                .authorName("Author")
                .created(LocalDateTime.of(2023, 2, 19, 14, 37, 20))
                .build();
    }

    public static ItemResponseForOwner getItemResponseForOwner() {
        return ItemResponseForOwner.builder()
                .id(1L)
                .description("Item description")
                .available(true)
                .name("Item")
                .comments(List.of(getCommentResponseDto()))
                .lastBooking(null)
                .nextBooking(null)
                .build();
    }


    public static ItemRequestResponseDto getItemRequestResponseDto() {
        return ItemRequestResponseDto.builder()
                .id(1L)
                .description("Description for request")
                .created(LocalDateTime.of(2023, 2, 19, 14, 37, 20))
                .build();
    }

    public static UserResponseDto getUserResponseDto() {
        return UserResponseDto.builder()
                .id(1L)
                .name("UserName")
                .email("email@yandex.ru")
                .build();
    }

    public static UserResponseDto getUpdatedUserResponseDto() {
        return UserResponseDto.builder()
                .id(1L)
                .name("UpdatedUserName")
                .email("updatedEmail@yandex.ru")
                .build();
    }

    public static CommentCreateDto getCommentCreateDto() {
        CommentCreateDto commentCreateDto = new CommentCreateDto();
        commentCreateDto.setText("comment");
        return commentCreateDto;
    }

}
