package ru.practicum.shareit.request.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemResponseForItemRequest;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestCreatorDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.exceptions.ItemRequestNotFound;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {

    ItemRequestMapper mapper;
    ItemRequestRepository repository;
    ItemMapper itemMapper;
    UserService userService;

    ItemService itemService;

    @Override
    @Transactional
    public ItemRequestResponseDto create(ItemRequestCreatorDto dto, long userId) {
        User creator = userService.getUser(userId);
        ItemRequest itemRequest = mapper.toItemRequest(dto);
        itemRequest.setCreator(creator);
        ItemRequest result = repository.save(itemRequest);
        return mapper.toItemRequestResponse(result);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestResponseDto> getByUser(long userId) {
        User user = userService.getUser(userId);
        List<ItemRequest> itemRequestList = repository.findAllByCreator(user);
        return getItemRequestResponseDtos(itemRequestList);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestResponseDto> getAll(long userId, int from, int size) {
        User user = userService.getUser(userId);
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "created"));
        Page<ItemRequest> requests = repository.findAllByCreatorNot(user, pageable);
        List<ItemRequest> itemRequestList = requests.getContent();
        return getItemRequestResponseDtos(itemRequestList);
    }

    private List<ItemRequestResponseDto> getItemRequestResponseDtos(List<ItemRequest> itemRequestList) {
        List<ItemRequestResponseDto> responseDtos = itemRequestList.stream()
                .map(mapper::toItemRequestResponse)
                .collect(Collectors.toList());
        List<List<Item>> items = itemRequestList.stream()
                .map(itemService::getItemsByRequest)
                .collect(Collectors.toList());
        for (int i = 0; i < responseDtos.size(); i++) {
            List<ItemResponseForItemRequest> itemResp = items.get(i).stream()
                    .map(itemMapper::itemToResponseForItemRequest)
                    .collect(Collectors.toList());
            responseDtos.get(i).setItems(itemResp);
        }
        return responseDtos;
    }

    @Override
    @Transactional(readOnly = true)
    public ItemRequestResponseDto getById(long userId, Long requestId) {
        userService.getUser(userId);
        ItemRequest result = getRequestById(requestId);
        ItemRequestResponseDto responseDto = mapper.toItemRequestResponse(result);
        List<ItemResponseForItemRequest> items = itemService.getItemsByRequest(result).stream()
                .map(itemMapper::itemToResponseForItemRequest)
                .collect(Collectors.toList());
        responseDto.setItems(items);
        return responseDto;
    }

    @Override
    @Transactional(readOnly = true)
    public ItemRequest getRequestById(Long requestId) {
        Optional<ItemRequest> request = repository.findById(requestId);
        if (request.isPresent()) {
            return request.get();
        } else {
            throw new ItemRequestNotFound(String.format("Запрос с id = %d не найден", requestId));
        }
    }

    @Override
    public void save(ItemRequest request) {
        repository.save(request);
    }
}
