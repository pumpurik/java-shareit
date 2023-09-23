package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    @MockBean
    private UserServiceImpl userService;
    private UserDto user1Dto;
    private UserDto user2Dto;
    private UserDto newUserDto;

    @BeforeEach
    void init() {
        objectMapper = new ObjectMapper();
        newUserDto = new UserDto();
        user1Dto = new UserDto(1, "name1", "name1@mail");
        user2Dto = new UserDto(2, "name2", "name2@mail");
    }


    @Test
    void createUser() throws Exception {
        when(userService.createUser(any())).thenReturn(user1Dto);
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUserDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value(user1Dto.getName()))
                .andExpect(jsonPath("$.email").value(user1Dto.getEmail()));
    }

    @Test
    void updateUser() throws Exception {
        when(userService.updateUser(any(), anyLong())).thenReturn(user1Dto);
        mockMvc.perform(patch("/users/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUserDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value(user1Dto.getName()))
                .andExpect(jsonPath("$.email").value(user1Dto.getEmail()));
    }

    @Test
    void getAllUsers() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of(user1Dto, user2Dto));
        mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.[0].id").value(1))
                .andExpect(jsonPath("$.[1].id").value(2))
                .andExpect(jsonPath("$.[0].name").value(user1Dto.getName()))
                .andExpect(jsonPath("$.[1].name").value(user2Dto.getName()))
                .andExpect(jsonPath("$.[0].email").value(user1Dto.getEmail()))
                .andExpect(jsonPath("$.[1].email").value(user2Dto.getEmail()));
    }

    @Test
    void getUserById() throws Exception {
        when(userService.getUserById(anyLong())).thenReturn(user1Dto);
        mockMvc.perform(get("/users/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value(user1Dto.getName()))
                .andExpect(jsonPath("$.email").value(user1Dto.getEmail()));
    }

    @Test
    void deleteUserById() throws Exception {
        mockMvc.perform(get("/users/{id}", 1))
                .andExpect(status().isOk());
    }
}