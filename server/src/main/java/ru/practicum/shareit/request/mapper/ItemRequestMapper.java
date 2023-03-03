package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.request.dto.ItemRequestCreatorDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;

public interface ItemRequestMapper {

    ItemRequest toItemRequest(ItemRequestCreatorDto dto);

    ItemRequestResponseDto toItemRequestResponse(ItemRequest itemRequest);
}
