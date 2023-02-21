package ru.practicum.shareit.item.service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingInfoInItem;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.BookStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.exceptions.IllegalUserException;
import ru.practicum.shareit.item.exceptions.ItemNotAvailableException;
import ru.practicum.shareit.item.exceptions.ItemNotFoundException;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    UserService userService;
    ItemRepository repository;
    ItemMapper mapper;
    CommentRepository commentsRepository;
    BookingRepository bookingRepository;
    CommentMapper commentMapper;
    BookingMapper bookingMapper;
    ItemRequestService itemRequestService;


    @Override
    @Transactional(readOnly = true)
    public List<ItemResponseForOwner> getAllByUserId(long userId, int from, int size) {
        userService.getById(userId);
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "id"));
        Page<Item> itemsPage = repository.findAllByOwner(userId, pageable);
        List<ItemResponseForOwner> items = itemsPage.getContent().stream()
                .map(mapper::itemToItemResponseForOwner)
                .collect(Collectors.toList());
        items.forEach(i -> i.setComments(getComments(i.getId())));
        items.forEach(i -> i.setLastBooking(getLastBooking(i.getId())));
        items.forEach(i -> i.setNextBooking(getNextBooking(i.getId())));
        return items;
    }

    private List<CommentResponseDto> getComments(Long itemId) {
        return commentsRepository.findAllByItem_Id(itemId).stream()
                .map(commentMapper::commentToCommentResponse)
                .collect(Collectors.toList());
    }

    private BookingInfoInItem getLastBooking(Long itemId) {
        Booking dto = bookingRepository.findFirstByItemIdAndEndBeforeOrderByEndDesc(itemId, LocalDateTime.now());
        if (dto == null) {
            return null;
        }
        return bookingMapper.bookingToBookingInfoInItem(dto);
    }

    private BookingInfoInItem getNextBooking(Long itemId) {
        Booking dto = bookingRepository.findFirstByItemIdAndStartAfterOrderByStart(itemId, LocalDateTime.now());
        if (dto == null) {
            return null;
        }
        return bookingMapper.bookingToBookingInfoInItem(dto);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemResponseForOwner getById(long itemId, long userId) {
        userService.getById(userId);
        Item item = getItem(itemId);
        ItemResponseForOwner response = mapper.itemToItemResponseForOwner(item);
        response.setComments(getComments(itemId));
        if (item.getOwner().getId() == userId) {
            response.setLastBooking(getLastBooking(itemId));
            response.setNextBooking(getNextBooking(itemId));
        }
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemResponseDto> getByNameOrDescription(String str, long userId, int from, int size) {
        userService.getById(userId);
        List<Item> items = new ArrayList<>();
        Pageable page = PageRequest.of(from / size, size);
        if (!str.isBlank() && !str.isEmpty()) {
            items = repository.findByDescriptionContainingIgnoreCaseOrNameContainingIgnoreCase(str, str, page)
                    .getContent();
        }
        List<ItemResponseDto> response = items.stream()
                .filter(Item::getAvailable)
                .map(mapper::itemToItemResponseDto)
                .collect(Collectors.toList());
        response.forEach(i -> i.setComments(getComments(i.getId())));
        return response;
    }

    @Override
    @Transactional
    public ItemResponseDto createItem(ItemCreateDto dto, long userId) {
        User user = userService.getUser(userId);
        Item item = mapper.itemCreateDtoToItem(dto);
        item.setOwner(user);
        ItemRequest request = null;
        Long requestId = dto.getRequestId();
        if (requestId != null) {
            request = itemRequestService.getRequestById(requestId);
        }
        item.setRequest(request);
        Item created = repository.save(item);
        return mapper.itemToItemResponseDto(created);
    }

    @Override
    @Transactional
    public ItemResponseDto updateItem(ItemUpdateDto dto, long userId, long itemId) {
        userService.getById(userId);
        Item item = getItem(itemId);
        Item updated;
        if (userId == item.getOwner().getId()) {
            updateItemFields(item, dto);
            item.setId(itemId);
            updated = repository.save(item);
        } else {
            throw new IllegalUserException(String
                    .format("Для обновления сведений о вещи (id = %d) нужно быть ее владельцем (id = %d)",
                            item.getOwner().getId(), userId));
        }
        return mapper.itemToItemResponseDto(updated);
    }

    private void updateItemFields(Item item, ItemUpdateDto dto) {
        if (dto.getName() != null) {
            item.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            item.setDescription(dto.getDescription());
        }
        if (dto.getAvailable() != null) {
            item.setAvailable(dto.getAvailable());
        }
    }

    public Item getItem(long itemId) {
        Item item;
        try {
            item = repository.getById(itemId);
        } catch (EntityNotFoundException e) {
            throw new ItemNotFoundException(String.format("Вещи c id = %d не найдено", itemId));
        }
        return item;
    }

    @Override
    @Transactional
    public void deleteItem(long id, long userId) {
        userService.getById(userId);
        Item item = getItem(id);
        if (userId == item.getOwner().getId()) {
            repository.deleteById(id);
        } else {
            throw new IllegalUserException(String
                    .format("Для удаления вещи (id = %d) нужно быть ее владельцем (id = %d)",
                            item.getOwner().getId(), userId));
        }
    }

    @Override
    @Transactional
    public CommentResponseDto createComment(CommentCreateDto dto, long itemId, long userId) {
        User author = userService.getUser(userId);
        Item item = getItem(itemId);
        Pageable page = PageRequest.of(0, 10);
        if (bookingRepository.findAllByBookerIdAndStatusInAndEndIsBeforeOrderByStartDesc(userId,
                List.of(BookStatus.APPROVED), LocalDateTime.now(), page).isEmpty()) {
            throw new ItemNotAvailableException("Для создания комментария необходимо хотя бы один раз арендовать вещь");
        }
        Comment comment = commentMapper.commentCreateDtoToComment(dto, author, item, LocalDateTime.now());
        Comment created = commentsRepository.save(comment);
        return commentMapper.commentToCommentResponse(created);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Item> getItemsByRequest(ItemRequest requestId) {
        return repository.findAllByRequest(requestId);
    }
}
