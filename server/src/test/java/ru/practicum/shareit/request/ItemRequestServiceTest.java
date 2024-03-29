package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.mapper.ItemMapperImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestCreatorDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.exceptions.ItemRequestNotFound;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.mapper.ItemRequestMapperImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.supplier.ObjectSupplier;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

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
    User user;
    ItemRequestCreatorDto itemRequestCreatorDto;
    ItemRequest itemRequest;

    ItemRepository itemRepository;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        mapper = new ItemRequestMapperImpl();
        repository = mock(ItemRequestRepository.class);
        itemMapper = new ItemMapperImpl();
        itemRepository = mock(ItemRepository.class);
        itemRequestService = new ItemRequestServiceImpl(mapper, repository, itemMapper, userService, itemRepository);
        user = ObjectSupplier.getDefaultUser();
        itemRequestCreatorDto = ObjectSupplier.getDefaultItemRequestCreateDto();
        itemRequest = ObjectSupplier.getDefaultItemRequest();
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
        Item item = ObjectSupplier.getDefaultItem();
        when(userService.getUser(any(Long.class)))
                .thenReturn(user);
        when(repository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(itemRequest));
        when(itemRepository.findAllByRequest(any(ItemRequest.class)))
                .thenReturn(List.of(item));
        ItemRequestResponseDto result = itemRequestService.getById(1,1L);

        assertNotNull(result);
        assertEquals(result.getCreated(), itemRequest.getCreated());
        assertEquals(result.getId(), itemRequest.getId());
        assertEquals(result.getDescription(), itemRequest.getDescription());
        assertEquals(result.getItems().get(0).getId(), item.getId());
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
