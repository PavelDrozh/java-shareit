package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ItemController {

    private final static String ID_PATH = "/{itemId}";
    private final static String SEARCH_PATH = "/search";
    private static final String USER_HEADER = "X-Sharer-User-Id";

    ItemService service;

    @GetMapping
    public List<ItemResponseDto> getAllByUser(@RequestHeader(USER_HEADER) long userId) {
        return service.getAllByUserId(userId);
    }

    @GetMapping(ID_PATH)
    public ItemResponseDto getById(@PathVariable long itemId, @RequestHeader(USER_HEADER) long userId) {
        return service.getById(itemId, userId);
    }

    @GetMapping(SEARCH_PATH)
    public List<ItemResponseDto> getByQuery(@RequestParam @NotBlank @NotEmpty String text,
                                            @RequestHeader(USER_HEADER) long userId) {
        return service.getByNameOrDescription(text, userId);
    }

    @PostMapping
    public ItemResponseDto create(@RequestBody @Valid @NotNull ItemCreateDto dto,
                                  @RequestHeader(USER_HEADER) long userId) {
        return service.createItem(dto, userId);
    }

    @PatchMapping(ID_PATH)
    public ItemResponseDto update(@RequestBody @NotNull ItemUpdateDto dto,
                                  @RequestHeader(USER_HEADER) long userId,
                                  @PathVariable long itemId) {
        return service.updateItem(dto, userId, itemId);
    }

    @DeleteMapping(ID_PATH)
    public ItemResponseDto delete(@PathVariable long itemId, @RequestHeader(USER_HEADER) long userId) {
        return service.deleteItem(itemId, userId);
    }
}
