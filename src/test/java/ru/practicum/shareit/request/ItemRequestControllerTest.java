package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestCreatorDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.exceptions.ItemRequestNotFound;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
@AutoConfigureMockMvc
public class ItemRequestControllerTest {

    @MockBean
    private ItemRequestService itemRequestService;

    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mvc;

    private ItemRequestCreatorDto createDto;

    private ItemRequestResponseDto responseDto;

    @BeforeEach
    void setUp() {
        createDto = new ItemRequestCreatorDto();
        createDto.setDescription("Description for request");

        responseDto = ItemRequestResponseDto.builder()
                .id(1L)
                .description("Description for request")
                .created(LocalDateTime.of(2023, 2,19,14,37, 20))
                .build();
    }

    @Test
    void createItemRequestTest() throws Exception {
        when(itemRequestService.create(any(ItemRequestCreatorDto.class), any(Long.class)))
                .thenReturn(responseDto);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(createDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(responseDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(responseDto.getDescription())))
                .andExpect(jsonPath("$.created", is(responseDto.getCreated().toString())));
    }

    @Test
    void getRequestsByUser() throws Exception {
        when(itemRequestService.getByUser(any(Long.class)))
                .thenReturn(List.of(responseDto));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(responseDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(responseDto.getDescription())))
                .andExpect(jsonPath("$[0].created", is(responseDto.getCreated().toString())));
    }

    @Test
    void getAllRequestsTest() throws Exception {
        when(itemRequestService.getAll(any(Long.class), any(Integer.class), any(Integer.class)))
                .thenReturn(List.of(responseDto));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(responseDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(responseDto.getDescription())))
                .andExpect(jsonPath("$[0].created", is(responseDto.getCreated().toString())));
    }

    @Test
    void getItemRequestByIdTest() throws Exception {
        when(itemRequestService.getById(any(Long.class), any(Long.class)))
                .thenReturn(responseDto);

        mvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(responseDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(responseDto.getDescription())))
                .andExpect(jsonPath("$.created", is(responseDto.getCreated().toString())));
    }

    @Test
    void getMethodArgumentNotValidExceptionTest() throws Exception {
        createDto.setDescription("");

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(createDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getIllegalRequestParamTest() throws Exception {
        mvc.perform(get("/requests/all?from=-1")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getItemRequestNotFoundTest() throws Exception {
        when(itemRequestService.getByUser(any(Long.class)))
                .thenThrow(ItemRequestNotFound.class);

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
