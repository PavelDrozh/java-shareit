package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.State;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Validated
public class BookingController {

    private static final String USER_HEADER = "X-Sharer-User-Id";
    private static final  String ID_PATH = "/{bookingId}";
    private static final  String OWNER_PATH = "/owner";

    BookingClient bookingClient;

    @PostMapping
    ResponseEntity<Object> createBooking(@RequestHeader(USER_HEADER) Long userId,
                                         @RequestBody @Valid BookingCreationDto dto) {
        log.info("Create booking with userId={}, dto={}", userId, dto.toString());
        return bookingClient.createBooking(userId, dto);
    }

    @PatchMapping(ID_PATH)
    ResponseEntity<Object> approveBooking(@RequestHeader(USER_HEADER) Long userId, @PathVariable Long bookingId,
                                      @RequestParam(name = "approved") Boolean isApproved) {
        log.info("Approve booking with userId={}, bookingId={}, isApproved={}", userId, bookingId, isApproved);
        return bookingClient.approveBooking(userId, bookingId, isApproved);
    }

    @GetMapping(ID_PATH)
    ResponseEntity<Object> getBooking(@RequestHeader(USER_HEADER) Long userId, @PathVariable Long bookingId) {
        log.info("Get booking with userId={}, bookingId={}", userId, bookingId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping
    ResponseEntity<Object> getUserBookings(@RequestHeader(USER_HEADER) Long userId,
                                             @RequestParam (defaultValue = "ALL") String state,
                                             @PositiveOrZero @RequestParam(required = false, defaultValue = "0") int from,
                                             @Positive @RequestParam(required = false, defaultValue = "10") int size) {
        State userState = State.from(state)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + state));
        log.info("Get bookings with state {}, userId={}, from={}, size={}", state, userId, from, size);
        return bookingClient.getBookings(userId, userState, from, size);
    }

    @GetMapping(OWNER_PATH)
    ResponseEntity<Object> getOwnerBookings(@RequestHeader(USER_HEADER) Long userId,
                                              @RequestParam (defaultValue = "ALL") String state,
                                              @PositiveOrZero @RequestParam(required = false, defaultValue = "0") int from,
                                              @Positive @RequestParam(required = false, defaultValue = "10") int size) {
        State userState = State.from(state)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + state));
        log.info("Get owners bookings with state {}, userId={}, from={}, size={}", state, userId, from, size);
        return bookingClient.getOwnersBookings(userId, userState, from, size);
    }
}
