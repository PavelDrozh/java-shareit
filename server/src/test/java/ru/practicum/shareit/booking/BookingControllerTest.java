package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.exceptions.BookingNotFoundException;
import ru.practicum.shareit.booking.exceptions.NotUpdatedStatusException;
import ru.practicum.shareit.booking.model.BookStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemInfoInBooking;
import ru.practicum.shareit.supplier.ObjectSupplier;
import ru.practicum.shareit.user.dto.UserResponseDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
public class BookingControllerTest {

    private static final  String SOURCE_PATH = "/bookings";
    private static final String USER_HEADER = "X-Sharer-User-Id";
    private static final  String ID_PATH = "/{bookingId}";
    private static final  String OWNER_PATH = "/owner";
    private static final String APPROVED_TRUE = "?approved=true";
    @MockBean
    BookingService service;

    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mvc;

    private BookingCreationDto creationDto;
    private BookingResponseDto responseDto;
    UserResponseDto user;
    ItemInfoInBooking itemInfoInBooking;

    @BeforeEach
    void setUp() {
        mapper.registerModule(new JavaTimeModule());
        creationDto = ObjectSupplier.getDefaultBookingCreateDto();
        user = ObjectSupplier.getDefaultUserResponse();
        itemInfoInBooking = ObjectSupplier.getDefaultItemInfo();
        responseDto = ObjectSupplier.getDefaultBookingResponseDto();
    }

    @Test
    void createBookingTest() throws Exception {
        when(service.createBooking(any(Long.class), any(BookingCreationDto.class)))
                .thenReturn(responseDto);

        mvc.perform(post(SOURCE_PATH)
                        .content(mapper.writeValueAsString(creationDto))
                        .header(USER_HEADER, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(responseDto.getId()), Long.class))
                .andExpect(jsonPath("$.item", is(notNullValue())))
                .andExpect(jsonPath("$.item.id", is(responseDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.booker", is(notNullValue())))
                .andExpect(jsonPath("$.booker.id", is(responseDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.status", is(responseDto.getStatus().toString())))
                .andExpect(jsonPath("$.start", is(responseDto.getStart().toString())))
                .andExpect(jsonPath("$.end", is(responseDto.getEnd().toString())));
    }

    @Test
    void approveBookingTest() throws Exception {
        responseDto.setStatus(BookStatus.APPROVED);
        when(service.approveBooking(any(Long.class), any(Long.class), any(Boolean.class)))
                .thenReturn(responseDto);

        mvc.perform(patch(SOURCE_PATH + ID_PATH + APPROVED_TRUE, 1)
                        .header(USER_HEADER, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(responseDto.getId()), Long.class))
                .andExpect(jsonPath("$.item", is(notNullValue())))
                .andExpect(jsonPath("$.item.id", is(responseDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.booker", is(notNullValue())))
                .andExpect(jsonPath("$.booker.id", is(responseDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.status", is(responseDto.getStatus().toString())))
                .andExpect(jsonPath("$.start", is(responseDto.getStart().toString())))
                .andExpect(jsonPath("$.end", is(responseDto.getEnd().toString())));
    }

    @Test
    void getBookingTest() throws Exception {
        when(service.getBookingById(any(Long.class), any(Long.class)))
                .thenReturn(responseDto);

        mvc.perform(get(SOURCE_PATH + ID_PATH, 1)
                        .header(USER_HEADER, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(responseDto.getId()), Long.class))
                .andExpect(jsonPath("$.item", is(notNullValue())))
                .andExpect(jsonPath("$.item.id", is(responseDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.booker", is(notNullValue())))
                .andExpect(jsonPath("$.booker.id", is(responseDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.status", is(responseDto.getStatus().toString())))
                .andExpect(jsonPath("$.start", is(responseDto.getStart().toString())))
                .andExpect(jsonPath("$.end", is(responseDto.getEnd().toString())));
    }

    @Test
    void getUserBookingsTest() throws Exception {
        when(service.getBookingsByOwner(any(Long.class), any(String.class), any(Integer.class), any(Integer.class)))
                .thenReturn(List.of(responseDto));

        mvc.perform(get(SOURCE_PATH + OWNER_PATH + "?state=ALL&from=0&size=10")
                        .header(USER_HEADER, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(responseDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].item", is(notNullValue())))
                .andExpect(jsonPath("$[0].item.id", is(responseDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[0].booker", is(notNullValue())))
                .andExpect(jsonPath("$[0].booker.id", is(responseDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[0].status", is(responseDto.getStatus().toString())))
                .andExpect(jsonPath("$[0].start", is(responseDto.getStart().toString())))
                .andExpect(jsonPath("$[0].end", is(responseDto.getEnd().toString())));
    }

    @Test
    void getMissingRequestHeaderExceptionTest() throws Exception {
        mvc.perform(get(SOURCE_PATH + OWNER_PATH)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getBookingNotFoundExceptionTest() throws Exception {
        when(service.getBookingById(any(Long.class), any(Long.class)))
                .thenThrow(BookingNotFoundException.class);

        mvc.perform(get(SOURCE_PATH + ID_PATH, 10)
                        .header(USER_HEADER, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getNotUpdatedStatusExceptionTest() throws Exception {
        when(service.approveBooking(any(Long.class), any(Long.class), any(Boolean.class)))
                .thenThrow(NotUpdatedStatusException.class);

        mvc.perform(patch(SOURCE_PATH+ ID_PATH + APPROVED_TRUE, 1)
                        .header(USER_HEADER, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
