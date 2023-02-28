package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.exceptions.BookingNotFoundException;
import ru.practicum.shareit.booking.exceptions.NotUpdatedStatusException;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.mapper.BookingMapperImpl;
import ru.practicum.shareit.booking.model.BookStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.exceptions.IllegalUserException;
import ru.practicum.shareit.item.exceptions.ItemNotAvailableException;
import ru.practicum.shareit.item.exceptions.ItemNotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapperImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.mapper.UserMapperImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BookingServiceTest {

    BookingService bookingService;
    BookingRepository bookingRepo;
    ItemServiceImpl itemService;
    UserService userService;
    BookingMapper mapper;
    BookingCreationDto creationDto;

    User user;
    Item item;
    Booking booking;

    @BeforeEach
    void setUp() {
        bookingRepo = mock(BookingRepository.class);
        itemService = mock(ItemServiceImpl.class);
        userService = mock(UserService.class);
        mapper = new BookingMapperImpl(new UserMapperImpl(), new ItemMapperImpl());
        bookingService = new BookingService(bookingRepo, itemService, userService, mapper);
        creationDto = new BookingCreationDto();
        creationDto.setItemId(1L);
        creationDto.setStart(LocalDateTime.of(2023, 3,19,14,37, 20));
        creationDto.setEnd(LocalDateTime.of(2023, 3,20,14,37, 20));
        user = new User();
        user.setId(1L);
        user.setName("User");
        user.setEmail("email@yandex.ru");
        item = new Item();
        item.setId(1L);
        item.setAvailable(true);
        item.setOwner(2L);
        item.setName("Item Name");
        item.setDescription("Item Description");
        item.setRequest(null);
        item.setComments(new ArrayList<>());
        booking = new Booking();
        booking.setId(1L);
        booking.setItem(item);
        booking.setStatus(BookStatus.WAITING);
        booking.setBooker(user);
        booking.setStart(creationDto.getStart());
        booking.setEnd(creationDto.getEnd());
    }

    @Test
    void createBookingTest() {
        when(userService.getUser(anyLong()))
                .thenReturn(user);
        when(itemService.getItem(anyLong()))
                .thenReturn(item);
        when(bookingRepo.save(any(Booking.class)))
                .thenReturn(booking);
        BookingResponseDto result = bookingService.createBooking(user.getId(), creationDto);

        checkResult(result);
        assertEquals(result.getStatus(), BookStatus.WAITING);
    }

    private void checkResult(BookingResponseDto result) {
        assertNotNull(result);
        assertEquals(result.getId(), booking.getId());
        assertEquals(result.getBooker().getId(), user.getId());
        assertEquals(result.getItem().getId(), item.getId());
        assertEquals(result.getEnd(), creationDto.getEnd());
        assertEquals(result.getStart(), creationDto.getStart());
    }

    @Test
    void approveBookingTest() {
        when(itemService.getItem(anyLong()))
                .thenReturn(item);
        when(bookingRepo.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking));
        Booking savedBooking = new Booking();
        savedBooking.setStatus(BookStatus.APPROVED);
        savedBooking.setId(1L);
        savedBooking.setItem(item);
        savedBooking.setBooker(user);
        savedBooking.setStart(creationDto.getStart());
        savedBooking.setEnd(creationDto.getEnd());
        when(bookingRepo.save(any(Booking.class)))
                .thenReturn(savedBooking);
        BookingResponseDto result = bookingService.approveBooking(item.getOwner(), booking.getId(), true);

        checkResult(result);
        assertEquals(result.getStatus(), BookStatus.APPROVED);
    }

    @Test
    void getBookingByIdTest() {
        when(bookingRepo.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking));

        BookingResponseDto result = bookingService.getBookingById(user.getId(), booking.getId());

        checkResult(result);
        assertEquals(result.getStatus(), booking.getStatus());
    }

    @Test
    void getAllBookingsByUserTest() {
        when(userService.getUser(anyLong()))
                .thenReturn(user);
        when(bookingRepo.findAllByBookerIdAndStatusInOrderByStartDesc(any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingResponseDto> result = bookingService.getBookingsByUser(user.getId(), "ALL", 0, 10);

        checkResultList(result);
        assertEquals(result.get(0).getStatus(), BookStatus.WAITING);
    }

    @Test
    void getPastBookingsByUserTest() {
        booking.setStatus(BookStatus.APPROVED);
        when(userService.getUser(anyLong()))
                .thenReturn(user);
        when(bookingRepo.findAllByBookerIdAndStatusInAndEndIsBeforeOrderByStartDesc(any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingResponseDto> result = bookingService.getBookingsByUser(user.getId(), "PAST", 0, 10);

        checkResultList(result);
        assertEquals(result.get(0).getStatus(), BookStatus.APPROVED);
    }

    @Test
    void getFutureBookingsByUserTest() {
        when(userService.getUser(anyLong()))
                .thenReturn(user);
        when(bookingRepo.findAllByBookerIdAndStatusInAndStartIsAfterOrderByStartDesc(any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingResponseDto> result = bookingService.getBookingsByUser(user.getId(), "FUTURE", 0, 10);

        checkResultList(result);
        assertEquals(result.get(0).getStatus(), BookStatus.WAITING);
    }

    @Test
    void getCurrentBookingsByUserTest() {
        booking.setStatus(BookStatus.APPROVED);
        when(userService.getUser(anyLong()))
                .thenReturn(user);
        when(bookingRepo.findAllByBookerIdAndStatusInAndStartIsBeforeAndEndIsAfterOrderByStartDesc(any(), any(), any(),
                any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingResponseDto> result = bookingService.getBookingsByUser(user.getId(), "CURRENT", 0, 10);

        checkResultList(result);
        assertEquals(result.get(0).getStatus(), BookStatus.APPROVED);
    }

    @Test
    void getWaitingBookingsByUserTest() {
        when(userService.getUser(anyLong()))
                .thenReturn(user);
        when(bookingRepo.findAllByBookerIdAndStatusInOrderByStartDesc(any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingResponseDto> result = bookingService.getBookingsByUser(user.getId(), "WAITING",
                0, 10);

        checkResultList(result);
        assertEquals(result.get(0).getStatus(), BookStatus.WAITING);
    }

    @Test
    void getRejectedBookingsByUserTest() {
        booking.setStatus(BookStatus.REJECTED);
        when(userService.getUser(anyLong()))
                .thenReturn(user);
        when(bookingRepo.findAllByBookerIdAndStatusInOrderByStartDesc(any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingResponseDto> result = bookingService.getBookingsByUser(user.getId(), "REJECTED",
                0, 10);

        checkResultList(result);
        assertEquals(result.get(0).getStatus(), BookStatus.REJECTED);
    }

    @Test
    void getAllBookingsByOwnerTest() {
        when(userService.getUser(anyLong()))
                .thenReturn(user);
        when(bookingRepo.findAllByItem_OwnerAndStatusInOrderByStartDesc(any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingResponseDto> result = bookingService.getBookingsByOwner(user.getId(), "ALL",
                0, 10);

        checkResultList(result);
        assertEquals(result.get(0).getStatus(), BookStatus.WAITING);
    }

    @Test
    void getPastBookingsByOwnerTest() {
        booking.setStatus(BookStatus.APPROVED);
        when(userService.getUser(anyLong()))
                .thenReturn(user);
        when(bookingRepo.findAllByItem_OwnerAndStatusInAndEndIsBeforeOrderByStartDesc(any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingResponseDto> result = bookingService.getBookingsByOwner(user.getId(), "PAST",
                0, 10);

        checkResultList(result);
        assertEquals(result.get(0).getStatus(), BookStatus.APPROVED);
    }

    @Test
    void getFutureBookingsByOwnerTest() {
        when(userService.getUser(anyLong()))
                .thenReturn(user);
        when(bookingRepo.findAllByItem_OwnerAndStatusInAndStartIsAfterOrderByStartDesc(any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingResponseDto> result = bookingService.getBookingsByOwner(user.getId(), "FUTURE",
                0, 10);

        checkResultList(result);
        assertEquals(result.get(0).getStatus(), BookStatus.WAITING);
    }

    @Test
    void getCurrentBookingsByOwnerTest() {
        booking.setStatus(BookStatus.APPROVED);
        when(userService.getUser(anyLong()))
                .thenReturn(user);
        when(bookingRepo.findAllByItem_OwnerAndStatusInAndStartIsBeforeAndEndIsAfterOrderByStartDesc(any(), any(),
                any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingResponseDto> result = bookingService.getBookingsByOwner(user.getId(), "CURRENT",
                0, 10);

        checkResultList(result);
        assertEquals(result.get(0).getStatus(), BookStatus.APPROVED);
    }

    @Test
    void getWaitingBookingsByOwnerTest() {
        when(userService.getUser(anyLong()))
                .thenReturn(user);
        when(bookingRepo.findAllByItem_OwnerAndStatusInOrderByStartDesc(any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingResponseDto> result = bookingService.getBookingsByOwner(user.getId(), "WAITING",
                0, 10);

        checkResultList(result);
        assertEquals(result.get(0).getStatus(), BookStatus.WAITING);
    }

    @Test
    void getRejectedBookingsByOwnerTest() {
        booking.setStatus(BookStatus.REJECTED);
        when(userService.getUser(anyLong()))
                .thenReturn(user);
        when(bookingRepo.findAllByItem_OwnerAndStatusInOrderByStartDesc(any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingResponseDto> result = bookingService.getBookingsByOwner(user.getId(), "REJECTED",
                0, 10);

        checkResultList(result);
        assertEquals(result.get(0).getStatus(), BookStatus.REJECTED);
    }

    private void checkResultList(List<BookingResponseDto> result) {
        assertNotNull(result);
        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getId(), booking.getId());
        assertEquals(result.get(0).getBooker().getId(), user.getId());
        assertEquals(result.get(0).getItem().getId(), item.getId());
        assertEquals(result.get(0).getEnd(), creationDto.getEnd());
        assertEquals(result.get(0).getStart(), creationDto.getStart());
    }

    @Test
    void getBookingNotFoundExceptionTest() {
        when(bookingRepo.findById(anyLong()))
                .thenReturn(Optional.empty());
        item.setOwner(1L);
        when(itemService.getItem(anyLong()))
                .thenReturn(item);

        assertThatThrownBy(() -> bookingService.approveBooking(user.getId(), 100L, false))
                .isInstanceOf(BookingNotFoundException.class)
                .message().isEqualTo("Бронирование с id = 100 не найдено");
        assertThatThrownBy(() -> bookingService.getBookingById(user.getId(), 100L))
                .isInstanceOf(BookingNotFoundException.class)
                .message().isEqualTo("Бронирование с id = 100 не найдено");
    }

    @Test
    void getItemNotFoundExceptionTest() {
        when(userService.getUser(anyLong()))
                .thenReturn(user);
        item.setOwner(user.getId());
        when(itemService.getItem(anyLong()))
                .thenReturn(item);

        assertThatThrownBy(() -> bookingService.createBooking(user.getId(), creationDto))
                .isInstanceOf(ItemNotFoundException.class)
                .message().isEqualTo("Владелец не должен бронировать бронировать свою вещь");
    }

    @Test
    void getItemNotAvailableExceptionTest() {
        when(userService.getUser(anyLong()))
                .thenReturn(user);
        item.setAvailable(false);
        when(itemService.getItem(anyLong()))
                .thenReturn(item);

        assertThatThrownBy(() -> bookingService.createBooking(user.getId(), creationDto))
                .isInstanceOf(ItemNotAvailableException.class)
                .message().isEqualTo(String.format("Вещь (%s) недоступна", item.getName()));
    }

    @Test
    void getIllegalUserExceptionTest() {
        when(itemService.getItem(anyLong()))
                .thenReturn(item);
        when(bookingRepo.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking));

        assertThatThrownBy(() -> bookingService.approveBooking(user.getId(), booking.getId(), true))
                .isInstanceOf(IllegalUserException.class)
                .message().isEqualTo("Подтвердить бронированиие может только владелец вещи");
    }

    @Test
    void getNotUpdatedStatusExceptionTest() {
        item.setOwner(user.getId());
        when(itemService.getItem(anyLong()))
                .thenReturn(item);
        booking.setStatus(BookStatus.APPROVED);
        when(bookingRepo.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking));

        assertThatThrownBy(() -> bookingService.approveBooking(user.getId(), booking.getId(), true))
                .isInstanceOf(NotUpdatedStatusException.class)
                .message().isEqualTo(String.format("Cтатус = %s, уже установлен", BookStatus.APPROVED.name()));

        booking.setStatus(BookStatus.REJECTED);
        assertThatThrownBy(() -> bookingService.approveBooking(user.getId(), booking.getId(), false))
                .isInstanceOf(NotUpdatedStatusException.class)
                .message().isEqualTo(String.format("Cтатус = %s, уже установлен", BookStatus.REJECTED.name()));
    }

    @Test
    void getBookingByIdThrowsUserNotFoundExceptionTest() {
        when(bookingRepo.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking));

        assertThatThrownBy(() -> bookingService.getBookingById(12L, booking.getId()))
                .isInstanceOf(UserNotFoundException.class)
                .message().isEqualTo("Просматривать бронирование могут владельцы вещи " +
                        "или завители на бронирование");
    }
}
