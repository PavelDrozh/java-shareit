package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.mapper.BookingMapperImpl;
import ru.practicum.shareit.booking.model.BookStatus;
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
    User user;

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

        user = new User();
        user.setId(1L);
        user.setName("User");
        user.setEmail("email@yandex.ru");

        item = new Item();
        item.setId(1L);
        item.setName("Item Name");
        item.setOwner(user);
        item.setDescription("Item Description");
        item.setAvailable(true);
        item.setRequest(null);
        item.setComments(new ArrayList<>());

        itemCreateDto = ItemCreateDto.builder()
                .name(item.getName())
                .description(item.getDescription())
                .requestId(1L)
                .available(item.getAvailable())
                .build();
    }

    @Test
    void getAllByUserTest() {
        when(userService.getUser(anyLong())).thenReturn(user);
        when(repository.findAllByOwner(any(User.class), any(Pageable.class)))
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
        User user = new User();
        user.setId(1L);
        user.setName("name");
        user.setEmail("name@yandex.ru");
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStart(LocalDateTime.now().minusDays(10));
        booking.setEnd(LocalDateTime.now().minusDays(9));
        booking.setId(1L);
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
        ItemRequest request = new ItemRequest();
        request.setId(1L);
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
        ItemUpdateDto updateDto = ItemUpdateDto.builder()
                .name("Updated Name")
                .description("Updated Description")
                .available(false)
                .build();
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
        User user = new User();
        user.setId(1L);
        user.setName("User");
        user.setEmail("email@yandex.ru");
        Booking booking = new Booking();
        booking.setStatus(BookStatus.APPROVED);
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStart(LocalDateTime.of(2023, 1,19,14,37, 20));
        booking.setEnd(LocalDateTime.of(2023, 1,20,14,37, 20));
        booking.setId(1L);
        Comment comment = new Comment();
        comment.setCreated(LocalDateTime.of(2023, 2,12,14,37, 20));
        comment.setText("comment");
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setId(1L);
        CommentCreateDto commentCreateDto = new CommentCreateDto();
        commentCreateDto.setText(comment.getText());
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
