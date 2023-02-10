package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;

import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
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
    List<BookingResponseDto> getUserBookings(@RequestHeader(USER_HEADER) Long userId,
                                             @RequestParam (defaultValue = "ALL") String state) {
        return service.getBookingsByUser(userId, state);
    }

    @GetMapping(OWNER_PATH)
    List<BookingResponseDto> getOwnerBookings(@RequestHeader(USER_HEADER) Long userId,
                                              @RequestParam (defaultValue = "ALL") String state) {
        return service.getBookingsByOwner(userId, state);
    }
}
