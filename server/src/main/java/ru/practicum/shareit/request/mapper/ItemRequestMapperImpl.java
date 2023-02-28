package ru.practicum.shareit.request.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.request.dto.ItemRequestCreatorDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.time.LocalDateTime;

@Component
public class ItemRequestMapperImpl implements ItemRequestMapper {

    @Override
    public ItemRequest toItemRequest(ItemRequestCreatorDto dto) {
        ItemRequest created = new ItemRequest();
        created.setDescription(dto.getDescription());
        created.setCreated(LocalDateTime.now());
        return created;
    }

    @Override
    public ItemRequestResponseDto toItemRequestResponse(ItemRequest itemRequest) {
        return ItemRequestResponseDto.builder()
                .created(itemRequest.getCreated())
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .build();
    }
}
