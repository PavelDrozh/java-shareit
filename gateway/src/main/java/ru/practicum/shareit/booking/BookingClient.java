package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.State;
import ru.practicum.shareit.booking.exceptions.IncorrectDateTimeException;
import ru.practicum.shareit.client.BaseClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";
    private static final String OWNER_PATH = "/owner";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> getBookings(long userId, State state, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "state", state.name(),
                "from", from,
                "size", size
        );
        return get("?state={state}&from={from}&size={size}", userId, parameters);
    }


    public ResponseEntity<Object> createBooking(long userId, BookingCreationDto requestDto) {
        checkDates(requestDto);
        return post("", userId, requestDto);
    }

    private void checkDates(BookingCreationDto bookingForSave) {
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

    public ResponseEntity<Object> getBooking(long userId, Long bookingId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> approveBooking(Long userId, Long bookingId, Boolean isApproved) {
        Map<String, Object> parameters = Map.of(
                "approved", isApproved);
        return patch(String.format("/%s?approved={approved}", bookingId), userId, parameters, null);
    }

    public ResponseEntity<Object> getOwnersBookings(long userId, State state, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "state", state.name(),
                "from", from,
                "size", size
        );
        return get(OWNER_PATH + "?state={state}&from={from}&size={size}", userId, parameters);
    }
}
