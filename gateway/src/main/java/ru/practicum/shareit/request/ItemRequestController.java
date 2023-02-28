package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreatorDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Validated
public class ItemRequestController {

    private static final String USER_HEADER = "X-Sharer-User-Id";
    private static final String ALL_PATH = "/all";
    private static final String ID_PATH = "/{requestId}";

    ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> createItemRequest(@RequestHeader(USER_HEADER) long userId,
                                                    @RequestBody @Valid ItemRequestCreatorDto dto) {
        return itemRequestClient.create(dto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getByUser(@RequestHeader(USER_HEADER) long userId) {
        return itemRequestClient.getByUser(userId);
    }

    @GetMapping(ALL_PATH)
    public ResponseEntity<Object> getAll(@RequestHeader(USER_HEADER) long userId,
                                               @PositiveOrZero @RequestParam(required = false, defaultValue = "0") int from,
                                               @Positive @RequestParam(required = false, defaultValue = "10") int size) {
        return itemRequestClient.getAll(userId, from, size);
    }

    @GetMapping(ID_PATH)
    public ResponseEntity<Object> getById(@RequestHeader(USER_HEADER) long userId,
                                          @PathVariable Long requestId) {
        return itemRequestClient.getById(userId, requestId);
    }
}
