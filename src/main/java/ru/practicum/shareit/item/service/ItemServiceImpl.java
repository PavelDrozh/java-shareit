package ru.practicum.shareit.item.service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.exceptions.IllegalUserException;
import ru.practicum.shareit.item.exceptions.ItemNotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    UserService userService;
    ItemRepository repository;
    ItemMapper mapper;

    @Override
    public List<ItemResponseDto> getAllByUserId(long userId) {
        userService.getById(userId);
        return repository.getAllByUserId(userId).stream()
                .map(mapper::itemToItemRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemResponseDto getById(long itemId, long userId) {
        userService.getById(userId);
        Item item = getItem(itemId);
        return mapper.itemToItemRequestDto(item);
    }

    @Override
    public List<ItemResponseDto> getByNameOrDescription(String str, long userId) {
        userService.getById(userId);
        List<Item> items = new ArrayList<>();
        if (!str.isBlank() && !str.isEmpty()) {
            items = repository.getByNameOrDescription(str);
        }
        return items.stream()
                .map(mapper::itemToItemRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemResponseDto createItem(ItemCreateDto dto, long userId) {
        userService.getById(userId);
        Item item = mapper.itemCreateDtoToItem(dto);
        item.setOwner(userId);
        Item created = repository.create(item);
        return mapper.itemToItemRequestDto(created);
    }

    @Override
    public ItemResponseDto updateItem(ItemUpdateDto dto, long userId, long itemId) {
        userService.getById(userId);
        Item item = getItem(itemId);
        Item updated;
        if (userId == item.getOwner()) {
            Item forUpdate = mapper.itemUpdateDtoToItem(dto);
            forUpdate.setId(itemId);
            updated = repository.update(forUpdate);
        } else {
            throw new IllegalUserException(String
                    .format("Для обновления сведений о вещи (id = %d) нужно быть ее владельцем (id = %d)",
                            item.getOwner(), userId));
        }
        return mapper.itemToItemRequestDto(updated);
    }

    private Item getItem(long itemId) {
        return repository.getById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Вещи с таким id не найдено"));
    }

    @Override
    public ItemResponseDto deleteItem(long id, long userId) {
        userService.getById(userId);
        Item item = getItem(id);
        Item deleted;
        if (userId == item.getOwner()) {
            deleted = repository.deleteById(id).orElse(null);
        } else {
            throw new IllegalUserException(String
                    .format("Для удаления вещи (id = %d) нужно быть ее владельцем (id = %d)",
                            item.getOwner(), userId));
        }
        return mapper.itemToItemRequestDto(deleted);
    }
}
