package ru.practicum.shareit.item.repository;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class InMemoryItemRepository implements ItemRepository {

    final Map<Long, Item> storage = new HashMap<>();
    long currentId;

    @Override
    public List<Item> getAllByUserId(long userId) {
        List<Item> usersItems = storage.values().stream()
                .filter(item -> item.getOwner() == userId)
                .collect(Collectors.toList());
        log.info("Упользователя {} количество вещей - {}", userId, usersItems.size());
        return usersItems;
    }

    @Override
    public List<Item> getByNameOrDescription(String str) {
        List<Item> items = storage.values().stream()
                .filter(item -> item.getName().toLowerCase().contains(str.toLowerCase()) ||
                        item.getDescription().toLowerCase().contains(str.toLowerCase()))
                .filter(Item::getAvailable)
                .collect(Collectors.toList());
        log.info("По подстроке найдено {} вещей", items.size());
        return items;
    }

    @Override
    public Optional<Item> getById(long id) {
        Optional<Item> item = Optional.ofNullable(storage.get(id));
        log.info("Вешь с id = {} найдена - {}", id, item.isPresent());
        return item;
    }

    @Override
    public Item create(Item item) {
        Item newItem = Item.builder()
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .id(++currentId)
                .owner(item.getOwner())
                .build();
        storage.put(newItem.getId(), newItem);
        log.info("Вещь создана - {}", newItem);
        return newItem;
    }

    @Override
    public Item update(Item item) {
        Item itemForUpdate = storage.get(item.getId());
        if (item.getName() != null && !item.getName().isBlank()) {
            itemForUpdate.setName(item.getName());
        }
        if (item.getDescription() != null && !item.getDescription().isBlank()) {
            itemForUpdate.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            itemForUpdate.setAvailable(item.getAvailable());
        }
        storage.put(item.getId(), itemForUpdate);
        log.info("Вещь обновлена - {}", itemForUpdate);
        return itemForUpdate;
    }

    @Override
    public Optional<Item> deleteById(long id) {
        Optional<Item> deleted = Optional.ofNullable(storage.remove(id));
        log.info("Вещь удалена - {}", deleted);
        return deleted;
    }
}
