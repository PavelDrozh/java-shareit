package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Page<Item> findAllByOwner(long owner, Pageable pageable);

    Page<Item> findByDescriptionContainingIgnoreCaseOrNameContainingIgnoreCase(String str, String st, Pageable page);

    List<Item> findAllByRequest(ItemRequest request);
}
