package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Validated
public class ItemController {

    private static final  String ID_PATH = "/{itemId}";
    private static final  String SEARCH_PATH = "/search";
    private static final  String COMMENT_PATH = "/comment";
    private static final String USER_HEADER = "X-Sharer-User-Id";

    ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> getAllByUser(@RequestHeader(USER_HEADER) long userId,
                                               @PositiveOrZero @RequestParam(required = false, defaultValue = "0")
                                                   int from,
                                               @Positive @RequestParam(required = false, defaultValue = "10")
                                                   int size) {
        log.info("Get all items by userId={}, from={}, size={}", userId, from, size);
        return itemClient.getAllByUserId(userId, from, size);
    }

    @GetMapping(ID_PATH)
    public ResponseEntity<Object> getById(@PathVariable long itemId, @RequestHeader(USER_HEADER) long userId) {
        log.info("Get item by userId={}, itemId={}", userId, itemId);
        return itemClient.getById(itemId, userId);
    }

    @GetMapping(SEARCH_PATH)
    public ResponseEntity<Object> getByQuery(@RequestParam(required = false, defaultValue = "") String text,
                                            @RequestHeader(USER_HEADER) long userId,
                                            @PositiveOrZero @RequestParam(required = false, defaultValue = "0")
                                                int from,
                                            @Positive @RequestParam(required = false, defaultValue = "10")
                                                int size) {
        log.info("Get items by text={}, userId={}, from={}, size={}", text, userId, from, size);
        return itemClient.getByNameOrDescription(text, userId, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(USER_HEADER) long userId,
                                         @RequestBody @Valid @NotNull ItemCreateDto dto) {
        log.info("Create item by userId={}, itemDto={}", userId, dto.toString());
        return itemClient.createItem(userId, dto);
    }

    @PatchMapping(ID_PATH)
    public ResponseEntity<Object> update(@RequestBody @NotNull ItemUpdateDto dto,
                                  @RequestHeader(USER_HEADER) long userId,
                                  @PathVariable long itemId) {
        log.info("Update item by userId={}, itemId={}, dto={}", userId, itemId, dto.toString());
        return itemClient.updateItem(dto, userId, itemId);
    }

    @DeleteMapping(ID_PATH)
    public ResponseEntity<Object> delete(@PathVariable long itemId, @RequestHeader(USER_HEADER) long userId) {
        log.info("Delete item by userId={}, itemId={}", userId, itemId);
        return itemClient.deleteItem(itemId, userId);
    }

    @PostMapping(ID_PATH + COMMENT_PATH)
    public ResponseEntity<Object> createComment(@RequestBody @Valid CommentCreateDto dto,
                                            @RequestHeader(USER_HEADER) long userId,
                                            @PathVariable long itemId) {
        log.info("Create comment by userId={}, itemId={}, commentDto={}", userId, itemId, dto.toString());
        return itemClient.createComment(dto, itemId,  userId);
    }
}
