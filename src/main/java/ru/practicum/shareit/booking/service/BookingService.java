package ru.practicum.shareit.booking.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
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
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
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
    ItemRepository itemRepository;
    UserRepository userRepository;
    BookingMapper mapper;

    public BookingResponseDto createBooking(Long userId, BookingCreationDto dto) {
        User booker = getUser(userId);
        Item item = getItem(dto.getItemId());
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

    private User getUser(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new UserNotFoundException(String.format("Пользователь с id = %d не найден", userId));
        }
        return user.get();
    }

    private Item getItem(Long itemId) {
        Optional<Item> item = itemRepository.findById(itemId);
        if (item.isEmpty()) {
            throw new ItemNotFoundException(String.format("Вещь id = %d не найдена", itemId));
        }
        return item.get();
    }

    private void checkDates(Booking bookingForSave) {
        if (bookingForSave.getStart().isBefore(LocalDateTime.now())) {
            throw new IncorrectDateTimeException("Дата начала бронирования не может быть раньше текущего времени");
        } else if (bookingForSave.getEnd().isBefore(LocalDateTime.now())) {
            throw new IncorrectDateTimeException("Дата кончания бронирования не может быть раньше текущего времени");
        } else if (bookingForSave.getEnd().isBefore(bookingForSave.getStart())) {
            throw new IncorrectDateTimeException("Дата кончания бронирования не может быть раньше " +
                    "времени начала бронирования");
        }
    }

    public BookingResponseDto approveBooking(Long userId, Long bookingId, Boolean isApproved) {
        Booking booking = getBooking(bookingId);
        Item item = getItem(booking.getItem().getId());
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

    public BookingResponseDto getBookingById(Long userId, Long bookingId) {
        Booking booking = getBooking(bookingId);
        if (booking.getBooker().getId().equals(userId) || booking.getItem().getOwner().equals(userId)) {
            return mapper.bookingToBookingResponseDto(booking);
        } else {
            throw new UserNotFoundException("Просматривать бронирование могут владельцы вещи " +
                    "или завители на бронирование");
        }
    }

    public List<BookingResponseDto> getBookingsByUser(Long userId, String state) {
        List<Booking> bookings = new ArrayList<>();
        List<BookStatus> statuses = new ArrayList<>();
        getUser(userId);
        checkState(state);
        switch (State.valueOf(state)) {
            case ALL:
                statuses = Arrays.stream(BookStatus.values()).collect(Collectors.toList());
                bookings = bookingRepo.findAllByBookerIdAndStatusInOrderByStartDesc(userId, statuses);
                break;
            case PAST:
                statuses.add(BookStatus.APPROVED);
                bookings = bookingRepo.findAllByBookerIdAndStatusInAndEndIsBeforeOrderByStartDesc(userId,
                        statuses, LocalDateTime.now());
                break;
            case FUTURE:
                statuses.add(BookStatus.APPROVED);
                statuses.add(BookStatus.WAITING);
                bookings = bookingRepo.findAllByBookerIdAndStatusInAndStartIsAfterOrderByStartDesc(userId,
                        statuses, LocalDateTime.now());
                break;
            case CURRENT:
                statuses.add(BookStatus.APPROVED);
                statuses.add(BookStatus.REJECTED);
                bookings = bookingRepo.findAllByBookerIdAndStatusInAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
                        userId, statuses, LocalDateTime.now(), LocalDateTime.now());
                break;
            case WAITING:
                statuses.add(BookStatus.WAITING);
                bookings = bookingRepo.findAllByBookerIdAndStatusInOrderByStartDesc(userId, statuses);
                break;
            case REJECTED:
                statuses.add(BookStatus.REJECTED);
                bookings = bookingRepo.findAllByBookerIdAndStatusInOrderByStartDesc(userId, statuses);
                break;
        }
        return  bookings.stream()
                .map(mapper::bookingToBookingResponseDto)
                .collect(Collectors.toList());
    }

    public List<BookingResponseDto> getBookingsByOwner(Long userId, String state) {
        List<Booking> bookings = new ArrayList<>();
        List<BookStatus> statuses = new ArrayList<>();
        getUser(userId);
        checkState(state);
        switch (State.valueOf(state)) {
            case ALL:
                statuses = Arrays.stream(BookStatus.values()).collect(Collectors.toList());
                bookings = bookingRepo.findAllByItem_OwnerAndStatusInOrderByStartDesc(userId, statuses);
                break;
            case PAST:
                statuses.add(BookStatus.APPROVED);
                bookings = bookingRepo.findAllByItem_OwnerAndStatusInAndEndIsBeforeOrderByStartDesc(userId,
                        statuses, LocalDateTime.now());
                break;
            case FUTURE:
                statuses.add(BookStatus.APPROVED);
                statuses.add(BookStatus.WAITING);
                bookings = bookingRepo.findAllByItem_OwnerAndStatusInAndStartIsAfterOrderByStartDesc(userId,
                        statuses, LocalDateTime.now());
                break;
            case CURRENT:
                statuses.add(BookStatus.APPROVED);
                statuses.add(BookStatus.REJECTED);
                bookings = bookingRepo.findAllByItem_OwnerAndStatusInAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
                        userId, statuses, LocalDateTime.now(), LocalDateTime.now());
                break;
            case WAITING:
                statuses.add(BookStatus.WAITING);
                bookings = bookingRepo.findAllByItem_OwnerAndStatusInOrderByStartDesc(userId, statuses);
                break;
            case REJECTED:
                statuses.add(BookStatus.REJECTED);
                bookings = bookingRepo.findAllByItem_OwnerAndStatusInOrderByStartDesc(userId, statuses);
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
            throw new IncorrectStateException("Unknown state: UNSUPPORTED_STATUS");
        }
    }
    //Пришлось использовать англоязычное описание ошибки, т.к. оно проверяется в тестах.
}
