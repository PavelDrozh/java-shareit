package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;

import java.util.List;

public interface ItemService {
    List<ItemResponseForOwner> getAllByUserId(long userId, int from, int size);

    ItemResponseForOwner getById(long itemId, long userId);

    List<ItemResponseDto> getByNameOrDescription(String str, long userId, int from, int size);

    ItemResponseDto createItem(ItemCreateDto dto, long userId);

    ItemResponseDto updateItem(ItemUpdateDto dto, long userId, long itemId);

    void deleteItem(long id, long userId);

    CommentResponseDto createComment(CommentCreateDto dto, long itemId, long userId);

}
