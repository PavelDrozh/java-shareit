package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemResponseForOwner;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;

@Component
public class ItemMapperImpl implements ItemMapper {
    @Override
    public Item itemCreateDtoToItem(ItemCreateDto dto) {
        Item item = new Item();
        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setAvailable(dto.getAvailable());
        return item;
    }

    @Override
    public Item itemUpdateDtoToItem(ItemUpdateDto dto) {
        Item item = new Item();
        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setAvailable(dto.getAvailable());
        item.setId(dto.getId());
        return item;
    }

    @Override
    public ItemResponseDto itemToItemResponseDto(Item item) {
        return ItemResponseDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    @Override
    public ItemResponseForOwner itemToItemResponseForOwner(Item item) {
        return ItemResponseForOwner.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }


}
