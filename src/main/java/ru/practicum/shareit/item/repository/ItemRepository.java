package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    List<Item> getAllByUserId(long userId);
    List<Item> getByNameOrDescription(String str);
    Optional<Item> getById(long id);
    Item create(Item item);
    Item update(Item item);
    Optional<Item> deleteById(long id);
}
