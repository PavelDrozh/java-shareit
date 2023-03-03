package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ItemController {

    private static final  String ID_PATH = "/{itemId}";
    private static final  String SEARCH_PATH = "/search";
    private static final  String COMMENT_PATH = "/comment";
    private static final String USER_HEADER = "X-Sharer-User-Id";

    ItemService service;

    @GetMapping
    public List<ItemResponseForOwner> getAllByUser(@RequestHeader(USER_HEADER) long userId, @RequestParam int from,
                                                   @RequestParam int size) {
        return service.getAllByUserId(userId, from, size);
    }

    @GetMapping(ID_PATH)
    public ItemResponseForOwner getById(@PathVariable long itemId, @RequestHeader(USER_HEADER) long userId) {
        return service.getById(itemId, userId);
    }

    @GetMapping(SEARCH_PATH)
    public List<ItemResponseDto> getByQuery(@RequestParam String text, @RequestHeader(USER_HEADER) long userId,
                                            @RequestParam int from, @RequestParam int size) {
        return service.getByNameOrDescription(text, userId, from, size);
    }

    @PostMapping
    public ItemResponseDto create(@RequestBody ItemCreateDto dto, @RequestHeader(USER_HEADER) long userId) {
        return service.createItem(dto, userId);
    }

    @PatchMapping(ID_PATH)
    public ItemResponseDto update(@RequestBody ItemUpdateDto dto, @RequestHeader(USER_HEADER) long userId,
                                  @PathVariable long itemId) {
        return service.updateItem(dto, userId, itemId);
    }

    @DeleteMapping(ID_PATH)
    public void delete(@PathVariable long itemId, @RequestHeader(USER_HEADER) long userId) {
        service.deleteItem(itemId, userId);
    }

    @PostMapping(ID_PATH + COMMENT_PATH)
    public CommentResponseDto createComment(@RequestBody CommentCreateDto dto, @RequestHeader(USER_HEADER) long userId,
                                            @PathVariable long itemId) {
        return service.createComment(dto, itemId,  userId);
    }
}
