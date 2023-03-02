package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.mapper.BookingMapperImpl;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.CommentMapperImpl;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.mapper.ItemMapperImpl;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.supplier.ObjectSupplier;
import ru.practicum.shareit.user.mapper.UserMapperImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class ItemServiceTest {

    ItemService itemService;
    UserService userService;
    ItemRepository repository;
    ItemMapper mapper;
    CommentRepository commentsRepository;
    BookingRepository bookingRepository;
    CommentMapper commentMapper;
    BookingMapper bookingMapper;
    ItemRequestService itemRequestService;
    Item item;
    ItemCreateDto itemCreateDto;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        repository = mock(ItemRepository.class);
        commentsRepository = mock(CommentRepository.class);
        bookingRepository = mock(BookingRepository.class);
        itemRequestService = mock(ItemRequestService.class);
        mapper = new ItemMapperImpl();
        commentMapper = new CommentMapperImpl();
        bookingMapper = new BookingMapperImpl(new UserMapperImpl(), mapper);
        itemService = new ItemServiceImpl(userService, repository, mapper, commentsRepository, bookingRepository,
                commentMapper, bookingMapper, itemRequestService);
        item = ObjectSupplier.getDefaultItem();
        itemCreateDto = ObjectSupplier.getDefaultItemCreateDto();
    }

    @Test
    void getAllByUserTest() {
        when(userService.getById(anyLong())).thenReturn(null);
        when(repository.findAllByOwner(anyLong(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(item)));
        when(commentsRepository.findAllByItem_Id(anyLong()))
                .thenReturn(new ArrayList<>());
        when(bookingRepository.findFirstByItemIdAndEndBeforeOrderByEndDesc(anyLong(), any(LocalDateTime.class)))
                .thenReturn(null);
        when(bookingRepository.findFirstByItemIdAndStartAfterOrderByStart(anyLong(), any(LocalDateTime.class)))
                .thenReturn(null);

        List<ItemResponseForOwner> result = itemService.getAllByUserId(1, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(result.get(0).getId(), item.getId());
        assertEquals(result.get(0).getName(), item.getName());
        assertEquals(result.get(0).getDescription(), item.getDescription());
        assertEquals(result.get(0).getAvailable(), item.getAvailable());
        assertEquals(result.get(0).getComments().size(), item.getComments().size());
        assertNull(result.get(0).getLastBooking());
        assertNull(result.get(0).getNextBooking());
    }

    @Test
    void getByIdTest() {
        when(userService.getById(anyLong())).thenReturn(null);
        when(repository.getById(anyLong()))
                .thenReturn(item);
        when(commentsRepository.findAllByItem_Id(anyLong()))
                .thenReturn(new ArrayList<>());
        Booking booking = ObjectSupplier.getDefaultBooking();
        booking.setStart(LocalDateTime.now().minusDays(10));
        booking.setEnd(LocalDateTime.now().minusDays(9));
        when(bookingRepository.findFirstByItemIdAndEndBeforeOrderByEndDesc(anyLong(), any(LocalDateTime.class)))
                .thenReturn(booking);
        when(bookingRepository.findFirstByItemIdAndStartAfterOrderByStart(anyLong(), any(LocalDateTime.class)))
                .thenReturn(null);

        ItemResponseForOwner result = itemService.getById(1, 1);

        assertNotNull(result);
        assertEquals(result.getId(), item.getId());
        assertEquals(result.getName(), item.getName());
        assertEquals(result.getDescription(), item.getDescription());
        assertEquals(result.getAvailable(), item.getAvailable());
        assertEquals(result.getComments().size(), item.getComments().size());
        assertNotNull(result.getLastBooking());
        assertNull(result.getNextBooking());
    }

    @Test
    void getByQueryTest() {
        when(userService.getById(anyLong())).thenReturn(null);
        when(repository.findByDescriptionContainingIgnoreCaseOrNameContainingIgnoreCase(anyString(), anyString(),
                any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(item)));
        when(commentsRepository.findAllByItem_Id(anyLong()))
                .thenReturn(new ArrayList<>());

        String query = "item";
        List<ItemResponseDto> result = itemService.getByNameOrDescription(query, 1, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(result.get(0).getId(), item.getId());
        assertEquals(result.get(0).getName(), item.getName());
        assertEquals(result.get(0).getDescription(), item.getDescription());
        assertEquals(result.get(0).getAvailable(), item.getAvailable());
        assertEquals(result.get(0).getComments().size(), item.getComments().size());
    }

    @Test
    void createItemTest() {
        ItemRequest request = ObjectSupplier.getDefaultItemRequest();
        item.setRequest(request);
        when(userService.getById(anyLong())).thenReturn(null);
        when(repository.save(any(Item.class)))
                .thenReturn(item);
        doNothing().when(itemRequestService).save(any(ItemRequest.class));
        when(itemRequestService.getRequestById(anyLong()))
                .thenReturn(request);


        ItemResponseDto result = itemService.createItem(itemCreateDto, 1L);

        checkResult(result);
        assertEquals(result.getRequestId(), item.getRequest().getId());
        assertNull(result.getComments());
    }

    @Test
    void updateItemTest() {
        ItemUpdateDto updateDto = ObjectSupplier.getDefaultItemUpdateDto();
        when(userService.getById(anyLong())).thenReturn(null);
        when(repository.getById(anyLong()))
                .thenReturn(item);
        item.setName(updateDto.getName());
        item.setDescription(updateDto.getDescription());
        item.setAvailable(updateDto.getAvailable());
        when(repository.save(any(Item.class)))
                .thenReturn(item);

        ItemResponseDto result = itemService.updateItem(updateDto, 1L, 1L);

        checkResult(result);
        assertNull(result.getComments());
    }

    private void checkResult(ItemResponseDto result) {
        assertNotNull(result);
        assertEquals(result.getId(), item.getId());
        assertEquals(result.getName(), item.getName());
        assertEquals(result.getDescription(), item.getDescription());
        assertEquals(result.getAvailable(), item.getAvailable());
    }

    @Test
    void deleteItemTest() {
        when(userService.getById(anyLong())).thenReturn(null);
        when(repository.getById(anyLong()))
                .thenReturn(item);
        doNothing().when(repository).deleteById(anyLong());

        itemService.deleteItem(1L, 1L);
        verify(repository, times(1)).deleteById(anyLong());
    }

    @Test
    void createCommentTest() {
        User user = ObjectSupplier.getDefaultUser();
        Booking booking = ObjectSupplier.getDefaultBooking();
        Comment comment = ObjectSupplier.getDefaultComment();
        CommentCreateDto commentCreateDto = ObjectSupplier.getCommentCreateDto();
        when(userService.getUser(anyLong())).thenReturn(user);
        when(repository.getById(anyLong()))
                .thenReturn(item);
        when(bookingRepository.findAllByBookerIdAndStatusInAndEndIsBeforeOrderByStartDesc(anyLong(),
                any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));
        when(commentsRepository.save(any(Comment.class)))
                .thenReturn(comment);

        CommentResponseDto result = itemService.createComment(commentCreateDto, 1L, 1L);

        assertNotNull(result);
        assertEquals(result.getId(), comment.getId());
        assertEquals(result.getCreated(), comment.getCreated());
        assertEquals(result.getText(), comment.getText());
        assertEquals(result.getAuthorName(), user.getName());
    }
}
