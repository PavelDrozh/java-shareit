package ru.practicum.shareit.booking.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.exceptions.BookingNotFoundException;
import ru.practicum.shareit.booking.exceptions.IncorrectDateTimeException;
import ru.practicum.shareit.booking.exceptions.IncorrectStateException;
import ru.practicum.shareit.booking.exceptions.NotUpdatedStatusException;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.BookStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.exceptions.IllegalUserException;
import ru.practicum.shareit.item.exceptions.ItemNotAvailableException;
import ru.practicum.shareit.item.exceptions.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@RequiredArgsConstructor
public class BookingService {

    BookingRepository bookingRepo;
    ItemServiceImpl itemService;
    UserService userService;
    BookingMapper mapper;

    @Transactional
    public BookingResponseDto createBooking(Long userId, BookingCreationDto dto) {
        User booker = userService.getUser(userId);
        Item item = itemService.getItem(dto.getItemId());
        if (!item.getAvailable()) {
            throw new ItemNotAvailableException(String.format("Вещь (%s) недоступна", item.getName()));
        }
        if (item.getOwner().equals(booker.getId())) {
            throw new ItemNotFoundException("Владелец не должен бронировать бронировать свою вещь");
        }
        Booking bookingForSave = mapper.creationBookingDtoToBooking(dto, item, booker);
        checkDates(bookingForSave);
        Booking newBooking = bookingRepo.save(bookingForSave);
        return mapper.bookingToBookingResponseDto(newBooking);
    }

    private void checkDates(Booking bookingForSave) {
        if (bookingForSave.getStart().isBefore(LocalDateTime.now())) {
            throw new IncorrectDateTimeException(String.format("Дата начала бронирования (%s) " +
                    "не может быть раньше текущего времени (%s)",
                    bookingForSave.getStart().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")),
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))));
        } else if (bookingForSave.getEnd().isBefore(LocalDateTime.now())) {
            throw new IncorrectDateTimeException(String.format("Дата окончания бронирования (%s)" +
                    "не может быть раньше текущего времени (%s)",
                    bookingForSave.getEnd().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")),
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))));
        } else if (bookingForSave.getEnd().isBefore(bookingForSave.getStart())) {
            throw new IncorrectDateTimeException(String.format("Дата окончания бронирования (%s) не может быть " +
                    "раньше времени начала бронирования (%s)",
                    bookingForSave.getEnd().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")),
                    bookingForSave.getStart().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))));
        }
    }

    @Transactional
    public BookingResponseDto approveBooking(Long userId, Long bookingId, Boolean isApproved) {
        Booking booking = getBooking(bookingId);
        Item item = itemService.getItem(booking.getItem().getId());
        if (!item.getOwner().equals(userId)) {
            throw new IllegalUserException("Подтвердить бронированиие может только владелец вещи");
        }
        if (isApproved) {
            setStatus(booking, BookStatus.APPROVED);
        } else {
            setStatus(booking, BookStatus.REJECTED);
        }
        Booking updated = bookingRepo.save(booking);
        return mapper.bookingToBookingResponseDto(updated);
    }

    private void setStatus(Booking booking, BookStatus status) {
        if (booking.getStatus().equals(status)) {
            throw new NotUpdatedStatusException(String.format("Cтатус = %s, уже установлен", status.name()));
        }
        booking.setStatus(status);
    }

    private Booking getBooking(Long bookingId) {
        Optional<Booking> booking = bookingRepo.findById(bookingId);
        if (booking.isEmpty()) {
            throw new BookingNotFoundException(String.format("Бронирование с id = %d не найдено", bookingId));
        }
        return booking.get();
    }

    @Transactional(readOnly = true)
    public BookingResponseDto getBookingById(Long userId, Long bookingId) {
        Booking booking = getBooking(bookingId);
        if (booking.getBooker().getId().equals(userId) || booking.getItem().getOwner().equals(userId)) {
            return mapper.bookingToBookingResponseDto(booking);
        } else {
            throw new UserNotFoundException("Просматривать бронирование могут владельцы вещи " +
                    "или завители на бронирование");
        }
    }

    @Transactional(readOnly = true)
    public List<BookingResponseDto> getBookingsByUser(Long userId, String state, int from, int size) {
        List<Booking> bookings = new ArrayList<>();
        List<BookStatus> statuses = new ArrayList<>();
        Pageable pageable = PageRequest.of(from / size, size);
        userService.getUser(userId);
        checkState(state);
        switch (State.valueOf(state)) {
            case ALL:
                statuses = Arrays.stream(BookStatus.values()).collect(Collectors.toList());
                bookings = bookingRepo.findAllByBookerIdAndStatusInOrderByStartDesc(userId, statuses, pageable)
                        .getContent();
                break;
            case PAST:
                statuses.add(BookStatus.APPROVED);
                bookings = bookingRepo.findAllByBookerIdAndStatusInAndEndIsBeforeOrderByStartDesc(userId,
                        statuses, LocalDateTime.now(), pageable)
                        .getContent();
                break;
            case FUTURE:
                statuses.add(BookStatus.APPROVED);
                statuses.add(BookStatus.WAITING);
                bookings = bookingRepo.findAllByBookerIdAndStatusInAndStartIsAfterOrderByStartDesc(userId,
                        statuses, LocalDateTime.now(), pageable)
                        .getContent();
                break;
            case CURRENT:
                statuses.add(BookStatus.APPROVED);
                statuses.add(BookStatus.REJECTED);
                bookings = bookingRepo.findAllByBookerIdAndStatusInAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
                        userId, statuses, LocalDateTime.now(), LocalDateTime.now(), pageable)
                        .getContent();
                break;
            case WAITING:
                statuses.add(BookStatus.WAITING);
                bookings = bookingRepo.findAllByBookerIdAndStatusInOrderByStartDesc(userId, statuses, pageable)
                        .getContent();
                break;
            case REJECTED:
                statuses.add(BookStatus.REJECTED);
                bookings = bookingRepo.findAllByBookerIdAndStatusInOrderByStartDesc(userId, statuses, pageable)
                        .getContent();
                break;
        }
        return  bookings.stream()
                .map(mapper::bookingToBookingResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BookingResponseDto> getBookingsByOwner(Long userId, String state, int from, int size) {
        List<Booking> bookings = new ArrayList<>();
        List<BookStatus> statuses = new ArrayList<>();
        Pageable pageable = PageRequest.of(from / size, size);
        userService.getUser(userId);
        checkState(state);
        switch (State.valueOf(state)) {
            case ALL:
                statuses = Arrays.stream(BookStatus.values()).collect(Collectors.toList());
                bookings = bookingRepo.findAllByItem_OwnerAndStatusInOrderByStartDesc(userId, statuses, pageable)
                        .getContent();
                break;
            case PAST:
                statuses.add(BookStatus.APPROVED);
                bookings = bookingRepo.findAllByItem_OwnerAndStatusInAndEndIsBeforeOrderByStartDesc(userId,
                        statuses, LocalDateTime.now(), pageable)
                        .getContent();
                break;
            case FUTURE:
                statuses.add(BookStatus.APPROVED);
                statuses.add(BookStatus.WAITING);
                bookings = bookingRepo.findAllByItem_OwnerAndStatusInAndStartIsAfterOrderByStartDesc(userId,
                        statuses, LocalDateTime.now(), pageable)
                        .getContent();
                break;
            case CURRENT:
                statuses.add(BookStatus.APPROVED);
                statuses.add(BookStatus.REJECTED);
                bookings = bookingRepo.findAllByItem_OwnerAndStatusInAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
                        userId, statuses, LocalDateTime.now(), LocalDateTime.now(), pageable)
                        .getContent();
                break;
            case WAITING:
                statuses.add(BookStatus.WAITING);
                bookings = bookingRepo.findAllByItem_OwnerAndStatusInOrderByStartDesc(userId, statuses, pageable)
                        .getContent();
                break;
            case REJECTED:
                statuses.add(BookStatus.REJECTED);
                bookings = bookingRepo.findAllByItem_OwnerAndStatusInOrderByStartDesc(userId, statuses, pageable)
                        .getContent();
                break;
        }
        return  bookings.stream()
                .map(mapper::bookingToBookingResponseDto)
                .collect(Collectors.toList());
    }

    private void checkState(String state) {
        try {
            State.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new IncorrectStateException(String.format("Unknown state: %s", state));
        }
    }
    //Пришлось использовать англоязычное описание ошибки, т.к. оно проверяется в тестах.
}
