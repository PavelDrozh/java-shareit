package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;

@Component
public class ItemMapperImpl implements ItemMapper {
    @Override
    public Item itemCreateDtoToItem(ItemCreateDto dto) {
        return Item.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .available(dto.getAvailable())
                .build();
    }

    @Override
    public Item itemUpdateDtoToItem(ItemUpdateDto dto) {
        return Item.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .available(dto.getAvailable())
                .id(dto.getId())
                .build();
    }

    @Override
    public ItemResponseDto itemToItemRequestDto(Item item) {
        return ItemResponseDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }
}
