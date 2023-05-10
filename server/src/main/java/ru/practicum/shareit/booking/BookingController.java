package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Validated
public class BookingController {

    private static final String USER_HEADER = "X-Sharer-User-Id";
    private static final  String ID_PATH = "/{bookingId}";
    private static final  String OWNER_PATH = "/owner";

    BookingService service;

    @PostMapping
    BookingResponseDto createBooking(@RequestHeader(USER_HEADER) Long userId, @RequestBody BookingCreationDto dto) {
        return service.createBooking(userId, dto);
    }

    @PatchMapping(ID_PATH)
    BookingResponseDto approveBooking(@RequestHeader(USER_HEADER) Long userId, @PathVariable Long bookingId,
                                      @RequestParam(name = "approved") Boolean isApproved) {
        return service.approveBooking(userId, bookingId, isApproved);
    }

    @GetMapping(ID_PATH)
    BookingResponseDto getBooking(@RequestHeader(USER_HEADER) Long userId, @PathVariable Long bookingId) {
        return service.getBookingById(userId, bookingId);
    }

    @GetMapping
    List<BookingResponseDto> getUserBookings(@RequestHeader(USER_HEADER) Long userId, @RequestParam String state,
                                             @RequestParam int from, @RequestParam int size) {
        return service.getBookingsByUser(userId, state, from, size);
    }

    @GetMapping(OWNER_PATH)
    List<BookingResponseDto> getOwnerBookings(@RequestHeader(USER_HEADER) Long userId, @RequestParam String state,
                                              @RequestParam int from, @RequestParam int size) {
        return service.getBookingsByOwner(userId, state, from, size);
    }
}
