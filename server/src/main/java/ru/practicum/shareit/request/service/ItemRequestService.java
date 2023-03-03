package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestCreatorDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestService {

    ItemRequestResponseDto create(ItemRequestCreatorDto dto, long userId);

    List<ItemRequestResponseDto> getByUser(long userId);

    List<ItemRequestResponseDto> getAll(long userId, int from, int size);

    ItemRequestResponseDto getById(long userId, Long requestId);

    ItemRequest getRequestById(Long requestId);

    void save(ItemRequest request);
}
