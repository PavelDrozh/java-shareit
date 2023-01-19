package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;

public interface ItemMapper {
    Item itemCreateDtoToItem(ItemCreateDto dto);
    Item itemUpdateDtoToItem(ItemUpdateDto dto);
    ItemResponseDto itemToItemRequestDto(Item item);
}
