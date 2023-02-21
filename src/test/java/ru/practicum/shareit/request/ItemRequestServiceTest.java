package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.mapper.ItemMapperImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestCreatorDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.exceptions.ItemRequestNotFound;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.mapper.ItemRequestMapperImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemRequestServiceTest {

    ItemRequestService itemRequestService;
    ItemRequestMapper mapper;
    ItemRequestRepository repository;
    ItemMapper itemMapper;
    UserService userService;
    ItemService itemService;
    User user;
    ItemRequestCreatorDto itemRequestCreatorDto;
    ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        itemService = mock(ItemService.class);
        mapper = new ItemRequestMapperImpl();
        repository = mock(ItemRequestRepository.class);
        itemMapper = new ItemMapperImpl();
        itemRequestService = new ItemRequestServiceImpl(mapper, repository, itemMapper, userService, itemService);
        user = new User();
        user.setId(1L);
        user.setName("User");
        user.setEmail("email@yandex.ru");
        itemRequestCreatorDto = new ItemRequestCreatorDto();
        itemRequestCreatorDto.setDescription("Request description");
        itemRequest = new ItemRequest();
        itemRequest.setCreator(user);
        itemRequest.setCreated(LocalDateTime.of(2023, 2,19,14,37, 20));
        itemRequest.setDescription(itemRequestCreatorDto.getDescription());
        itemRequest.setId(1L);
    }

    @Test
    void createRequestTest() {
        when(userService.getUser(any(Long.class)))
                .thenReturn(user);
        when(repository.save(any(ItemRequest.class)))
                .thenReturn(itemRequest);
        ItemRequestResponseDto result = itemRequestService.create(itemRequestCreatorDto, user.getId());

        assertNotNull(result);
        assertEquals(result.getCreated(), itemRequest.getCreated());
        assertEquals(result.getId(), itemRequest.getId());
        assertEquals(result.getDescription(), itemRequest.getDescription());
    }

    @Test
    void getRequestsByUserTest() {
        when(userService.getUser(any(Long.class)))
                .thenReturn(user);
        when(repository.findAllByCreator(any(User.class)))
                .thenReturn(List.of(itemRequest));
        List<ItemRequestResponseDto> result = itemRequestService.getByUser(user.getId());

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(result.get(0).getCreated(), itemRequest.getCreated());
        assertEquals(result.get(0).getId(), itemRequest.getId());
        assertEquals(result.get(0).getDescription(), itemRequest.getDescription());
    }

    @Test
    void getAllRequestsTest() {
        user.setId(2L);
        when(userService.getUser(any(Long.class)))
                .thenReturn(user);
        when(repository.findAllByCreatorNot(any(User.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(itemRequest)));
        List<ItemRequestResponseDto> result = itemRequestService.getAll(2, 0,10);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(result.get(0).getCreated(), itemRequest.getCreated());
        assertEquals(result.get(0).getId(), itemRequest.getId());
        assertEquals(result.get(0).getDescription(), itemRequest.getDescription());
    }

    @Test
    void getRequestByIdTest() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Item Name");
        item.setOwner(user);
        item.setDescription("Item Description");
        item.setAvailable(true);
        item.setRequest(itemRequest);
        item.setComments(new ArrayList<>());
        when(userService.getUser(any(Long.class)))
                .thenReturn(user);
        when(repository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(itemRequest));
        ItemRequestResponseDto result = itemRequestService.getById(1,1L);

        assertNotNull(result);
        assertEquals(result.getCreated(), itemRequest.getCreated());
        assertEquals(result.getId(), itemRequest.getId());
        assertEquals(result.getDescription(), itemRequest.getDescription());
    }

    @Test
    void getItemRequestNotFoundTest() {
        when(userService.getUser(any(Long.class)))
                .thenReturn(user);
        when(repository.findById(any(Long.class)))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemRequestService.getById(1,10L))
                .isInstanceOf(ItemRequestNotFound.class)
                .message().isEqualTo(String.format("Запрос с id = %d не найден", 10));
    }
}
