package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.List;

public interface ItemService {
    List<ItemResponseDto> getAllByUserId(long userId);
    ItemResponseDto getById(long itemId, long userId);
    List<ItemResponseDto> getByNameOrDescription(String str, long userId);
    ItemResponseDto createItem(ItemCreateDto dto, long userId);
    ItemResponseDto updateItem(ItemUpdateDto dto, long userId, long itemId);
    ItemResponseDto deleteItem(long id, long userId);
}
