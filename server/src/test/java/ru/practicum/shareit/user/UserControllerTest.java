package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.supplier.ObjectSupplier;
import ru.practicum.shareit.user.dto.UserCreatorDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.exceptions.ExistingEmailException;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
public class UserControllerTest {
    private static final  String SOURCE_PATH = "/users";
    private static final  String ID_PATH = "/{userId}";
    public static final String PAGINATION_PARAMS = "from=0&size=10";
    @MockBean
    private UserService userService;

    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mvc;

    private UserResponseDto userDto;

    private UserCreatorDto userCreatorDto;
    private UserResponseDto updatedUserDto;

    private UserUpdateDto dtoForUpdating;

    @BeforeEach
    void setUp() {
        userDto = ObjectSupplier.getUserResponseDto();
        userCreatorDto = ObjectSupplier.getDefaultUserCreator();
        updatedUserDto = ObjectSupplier.getUpdatedUserResponseDto();
        dtoForUpdating = ObjectSupplier.getDefaultUserUpdate();
    }

    @Test
    void saveNewUser() throws Exception {
        when(userService.createUser(any()))
                .thenReturn(userDto);

        mvc.perform(post(SOURCE_PATH)
                        .content(mapper.writeValueAsString(userCreatorDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    void getAllUsers() throws Exception {
        when(userService.getAll(0, 10))
                .thenReturn(List.of(userDto));

        mvc.perform(get(SOURCE_PATH + "?" + PAGINATION_PARAMS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(userDto.getName())))
                .andExpect(jsonPath("$[0].email", is(userDto.getEmail())));
    }

    @Test
    void updateUser() throws Exception {
        when(userService.updateUser(any(UserUpdateDto.class), any(Long.class)))
                .thenReturn(updatedUserDto);

        mvc.perform(patch(SOURCE_PATH + ID_PATH, 1)
                        .content(mapper.writeValueAsString(dtoForUpdating))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(updatedUserDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(updatedUserDto.getName())))
                .andExpect(jsonPath("$.email", is(updatedUserDto.getEmail())));
    }

    @Test
    void getById() throws Exception {
        when(userService.getById(any(Long.class)))
                .thenReturn(userDto);

        mvc.perform(get(SOURCE_PATH + ID_PATH, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    void deleteById() throws Exception {
        doNothing().when(userService).deleteUser(any(Long.class));
        mvc.perform(delete(SOURCE_PATH + ID_PATH, 1))
                .andExpect(status().isOk());
        verify(userService, times(1)).deleteUser(any(Long.class));
    }

    @Test
    void getUserNotFoundExceptionTest() throws Exception {
        when(userService.getById(any(Long.class)))
                .thenThrow(UserNotFoundException.class);

        mvc.perform(get(SOURCE_PATH + ID_PATH, 100)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getExistingEmailExceptionTest() throws Exception {
        when(userService.createUser(any(UserCreatorDto.class)))
                .thenThrow(ExistingEmailException.class);

        mvc.perform(post(SOURCE_PATH)
                        .content(mapper.writeValueAsString(userCreatorDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }
}
