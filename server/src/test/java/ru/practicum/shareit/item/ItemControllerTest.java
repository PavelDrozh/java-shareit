package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.exceptions.IllegalUserException;
import ru.practicum.shareit.item.exceptions.ItemNotAvailableException;
import ru.practicum.shareit.item.exceptions.ItemNotFoundException;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.supplier.ObjectSupplier;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc
public class ItemControllerTest {

    @MockBean
    ItemService service;

    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mvc;

    private ItemCreateDto createDto;
    private ItemUpdateDto updateDto;
    private ItemResponseDto responseDto;
    private ItemResponseDto updatedResponseDto;
    private ItemResponseForOwner responseForOwner;
    private CommentCreateDto commentCreateDto;
    private CommentResponseDto commentResponseDto;


    @BeforeEach
    void setUp() {
        createDto = ObjectSupplier.getDefaultItemCreateDto();
        updateDto = ObjectSupplier.getDefaultItemUpdateDto();
        responseDto = ObjectSupplier.getDefaultItemResponseDto();
        updatedResponseDto = ObjectSupplier.getUpdatedItemResponseDto();
        commentCreateDto = ObjectSupplier.getCommentCreateDto();
        commentResponseDto = ObjectSupplier.getCommentResponseDto();
        responseForOwner = ObjectSupplier.getItemResponseForOwner();
    }

    @Test
    void createItemTest() throws Exception {
        when(service.createItem(any(ItemCreateDto.class), any(Long.class)))
                .thenReturn(responseDto);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(createDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(responseDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(responseDto.getDescription())))
                .andExpect(jsonPath("$.name", is(responseDto.getName())))
                .andExpect(jsonPath("$.available", is(responseDto.getAvailable())));
    }

    @Test
    void getAllByUserTest() throws Exception {
        when(service.getAllByUserId(any(Long.class), any(Integer.class), any(Integer.class)))
                .thenReturn(List.of(responseForOwner));

        mvc.perform(get("/items?from=0&size=10")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(responseForOwner.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(responseForOwner.getDescription())))
                .andExpect(jsonPath("$[0].name", is(responseForOwner.getName())))
                .andExpect(jsonPath("$[0].available", is(responseForOwner.getAvailable())))
                .andExpect(jsonPath("$[0].comments", is(notNullValue())));
    }

    @Test
    void getByIdTest() throws Exception {
        when(service.getById(any(Long.class), any(Long.class)))
                .thenReturn(responseForOwner);

        mvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(responseForOwner.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(responseForOwner.getDescription())))
                .andExpect(jsonPath("$.name", is(responseForOwner.getName())))
                .andExpect(jsonPath("$.available", is(responseForOwner.getAvailable())))
                .andExpect(jsonPath("$.comments", is(notNullValue())));
    }

    @Test
    void getByQueryTest() throws Exception {
        when(service.getByNameOrDescription(any(String.class), any(Long.class), any(Integer.class), any(Integer.class)))
                .thenReturn(List.of(responseDto));

        mvc.perform(get("/items/search?text=item&from=0&size=10")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(responseDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(responseDto.getDescription())))
                .andExpect(jsonPath("$[0].name", is(responseDto.getName())))
                .andExpect(jsonPath("$[0].available", is(responseDto.getAvailable())));
    }

    @Test
    void updateItemTest() throws Exception {
        when(service.updateItem(any(ItemUpdateDto.class), any(Long.class), any(Long.class)))
                .thenReturn(updatedResponseDto);

        mvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(updateDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(updatedResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(updatedResponseDto.getDescription())))
                .andExpect(jsonPath("$.name", is(updatedResponseDto.getName())))
                .andExpect(jsonPath("$.available", is(updatedResponseDto.getAvailable())));
    }

    @Test
    void deleteById() throws Exception {
        mvc.perform(delete("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void createCommentTest() throws Exception {
        when(service.createComment(any(CommentCreateDto.class), any(Long.class), any(Long.class)))
                .thenReturn(commentResponseDto);

        mvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(commentCreateDto))
                        .header("X-Sharer-User-Id", 2)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentResponseDto.getText())))
                .andExpect(jsonPath("$.authorName", is(commentResponseDto.getAuthorName())))
                .andExpect(jsonPath("$.created", is(commentResponseDto.getCreated().toString())));
    }

    @Test
    void getIllegalUserExceptionTest() throws Exception {
        when(service.updateItem(any(ItemUpdateDto.class), anyLong(), anyLong()))
                .thenThrow(IllegalUserException.class);

        mvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(updateDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getItemNotFoundExceptionTest() throws Exception {
        when(service.getById(anyLong(), anyLong()))
                .thenThrow(ItemNotFoundException.class);

        mvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getItemNotAvailableExceptionTest() throws Exception {
        when(service.createComment(any(CommentCreateDto.class), anyLong(), anyLong()))
                .thenThrow(ItemNotAvailableException.class);

        mvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(commentCreateDto))
                        .header("X-Sharer-User-Id", 2)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
