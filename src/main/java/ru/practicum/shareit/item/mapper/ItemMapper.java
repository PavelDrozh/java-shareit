package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;

public interface ItemMapper {
    Item itemCreateDtoToItem(ItemCreateDto dto);

    Item itemUpdateDtoToItem(ItemUpdateDto dto);

    ItemResponseDto itemToItemResponseDto(Item item);

    ItemResponseForOwner itemToItemResponseForOwner(Item item);

    ItemInfoInBooking itemToItemInfo(Item item);

}
