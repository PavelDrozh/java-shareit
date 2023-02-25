package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreatorDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.List;

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

    ItemRequestService service;

    @PostMapping
    public ItemRequestResponseDto createItemRequest(@RequestHeader(USER_HEADER) long userId,
                                                    @RequestBody @Valid ItemRequestCreatorDto dto) {
        return service.create(dto, userId);
    }

    @GetMapping
    public List<ItemRequestResponseDto> getByUser(@RequestHeader(USER_HEADER) long userId) {
        return service.getByUser(userId);
    }

    @GetMapping(ALL_PATH)
    public List<ItemRequestResponseDto> getAll(@RequestHeader(USER_HEADER) long userId,
                                               @PositiveOrZero @RequestParam(required = false, defaultValue = "0") int from,
                                               @Positive @RequestParam(required = false, defaultValue = "10") int size) {
        return service.getAll(userId, from, size);
    }

    @GetMapping(ID_PATH)
    public ItemRequestResponseDto getById(@RequestHeader(USER_HEADER) long userId,
                                          @PathVariable Long requestId) {
        return service.getById(userId, requestId);
    }
}
