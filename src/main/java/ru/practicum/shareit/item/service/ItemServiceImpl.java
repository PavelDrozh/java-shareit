package ru.practicum.shareit.item.service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingInfoInItem;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.BookStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemResponseForOwner;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.exceptions.IllegalUserException;
import ru.practicum.shareit.item.exceptions.ItemNotAvailableException;
import ru.practicum.shareit.item.exceptions.ItemNotFoundException;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
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

    @Override
    public List<ItemResponseForOwner> getAllByUserId(long userId) {
        userService.getById(userId);
        List<ItemResponseForOwner> items = repository.findAllByOwner(userId).stream()
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
    public ItemResponseForOwner getById(long itemId, long userId) {
        userService.getById(userId);
        Item item = getItem(itemId);
        ItemResponseForOwner response = mapper.itemToItemResponseForOwner(item);
        response.setComments(getComments(itemId));
        if (item.getOwner() == userId) {
            response.setLastBooking(getLastBooking(itemId));
            response.setNextBooking(getNextBooking(itemId));
        }
        return response;
    }

    @Override
    public List<ItemResponseDto> getByNameOrDescription(String str, long userId) {
        userService.getById(userId);
        List<Item> items = new ArrayList<>();
        if (!str.isBlank() && !str.isEmpty()) {
            items = repository.findByDescriptionContainingIgnoreCaseOrNameContainingIgnoreCase(str, str);
        }
        List<ItemResponseDto> response = items.stream()
                .filter(Item::getAvailable)
                .map(mapper::itemToItemResponseDto)
                .collect(Collectors.toList());
        response.forEach(i -> i.setComments(getComments(i.getId())));
        return response;
    }

    @Override
    public ItemResponseDto createItem(ItemCreateDto dto, long userId) {
        userService.getById(userId);
        Item item = mapper.itemCreateDtoToItem(dto);
        item.setOwner(userId);
        Item created = repository.save(item);
        return mapper.itemToItemResponseDto(created);
    }

    @Override
    public ItemResponseDto updateItem(ItemUpdateDto dto, long userId, long itemId) {
        userService.getById(userId);
        Item item = getItem(itemId);
        Item updated;
        if (userId == item.getOwner()) {
            updateItemFields(item, dto);
            item.setId(itemId);
            updated = repository.save(item);
        } else {
            throw new IllegalUserException(String
                    .format("Для обновления сведений о вещи (id = %d) нужно быть ее владельцем (id = %d)",
                            item.getOwner(), userId));
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

    private Item getItem(long itemId) {
        Item item;
        try {
            item = repository.getById(itemId);
        } catch (EntityNotFoundException e) {
            throw new ItemNotFoundException("Вещи с таким id не найдено");
        }
        return item;
    }

    @Override
    public void deleteItem(long id, long userId) {
        userService.getById(userId);
        Item item = getItem(id);
        if (userId == item.getOwner()) {
            repository.deleteById(id);
        } else {
            throw new IllegalUserException(String
                    .format("Для удаления вещи (id = %d) нужно быть ее владельцем (id = %d)",
                            item.getOwner(), userId));
        }
    }

    @Override
    public CommentResponseDto createComment(CommentCreateDto dto, long itemId, long userId) {
        User author = userService.getUser(userId);
        Item item = getItem(itemId);
        if (bookingRepository.findAllByBookerIdAndStatusInAndEndIsBeforeOrderByStartDesc
                (userId, List.of(BookStatus.APPROVED), LocalDateTime.now()).isEmpty()) {
            throw new ItemNotAvailableException("Для создания комментария необходимо хотя бы один раз арендовать вещь");
        }
        Comment comment = commentMapper.commentCreateDtoToComment(dto, author, item, LocalDateTime.now());
        Comment created = commentsRepository.save(comment);
        return commentMapper.commentToCommentResponse(created);
    }
}
