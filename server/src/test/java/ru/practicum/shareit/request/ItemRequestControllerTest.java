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
import ru.practicum.shareit.supplier.ObjectSupplier;

import java.nio.charset.StandardCharsets;
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

    private static final  String SOURCE_PATH = "/requests";
    private static final String USER_HEADER = "X-Sharer-User-Id";
    private static final  String ID_PATH = "/{requestId}";
    public static final String PAGINATION_PARAMS = "from=0&size=10";

    @MockBean
    private ItemRequestService itemRequestService;

    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mvc;

    private ItemRequestCreatorDto createDto;

    private ItemRequestResponseDto responseDto;

    @BeforeEach
    void setUp() {
        createDto = ObjectSupplier.getDefaultItemRequestCreateDto();

        responseDto = ObjectSupplier.getItemRequestResponseDto();
    }

    @Test
    void createItemRequestTest() throws Exception {
        when(itemRequestService.create(any(ItemRequestCreatorDto.class), any(Long.class)))
                .thenReturn(responseDto);

        mvc.perform(post(SOURCE_PATH)
                        .content(mapper.writeValueAsString(createDto))
                        .header(USER_HEADER, 1)
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

        mvc.perform(get(SOURCE_PATH)
                        .header(USER_HEADER, 1)
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

        mvc.perform(get(SOURCE_PATH + "/all?" + PAGINATION_PARAMS)
                        .header(USER_HEADER, 1)
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

        mvc.perform(get(SOURCE_PATH + ID_PATH, 1)
                        .header(USER_HEADER, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(responseDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(responseDto.getDescription())))
                .andExpect(jsonPath("$.created", is(responseDto.getCreated().toString())));
    }

    @Test
    void getItemRequestNotFoundTest() throws Exception {
        when(itemRequestService.getByUser(any(Long.class)))
                .thenThrow(ItemRequestNotFound.class);

        mvc.perform(get(SOURCE_PATH)
                        .header(USER_HEADER, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
