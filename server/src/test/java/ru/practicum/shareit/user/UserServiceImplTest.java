package ru.practicum.shareit.user;

import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class UserServiceImplTest {
    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserRepository userRepository;
    private UserDto user1Dto;
    private UserDto user2Dto;
    private UserDto userErrorDto;
    private User user1;
    private User user2;

    @BeforeEach
    void init() {
        user1 = new User(1, "name1", "email@1");
        user2 = new User(2, "name2", "email@2");
        user1Dto = new UserDto(1, "name1", "email@1");
        user2Dto = new UserDto(2, "name2", "email@1");
        userErrorDto = new UserDto(2, "nameError", null);
    }

    @Test
    void createUser() throws ValidationException {
        when(userRepository.save(any())).thenReturn(user1);
        UserDto user = userService.createUser(user1Dto);

        assertThat(user.getId()).isEqualTo(1);
        assertThat(user.getName()).isEqualTo("name1");
        assertThat(user.getEmail()).isEqualTo("email@1");
    }

    @Test
    void createUserWithTheWrongEmail() {
        Assertions.assertThrows(ValidationException.class, () -> {
            userService.createUser(userErrorDto);
        }, "Электронная почта не может быть пустой и должна содержать символ @");
    }

    @Test
    void createUserExpectConstraintViolationException() {
        when(userRepository.save(any())).thenThrow(ConstraintViolationException.class);
        when(userRepository.findByEmailContaining(anyString())).thenReturn(Optional.empty());
        Assertions.assertThrows(ValidationException.class, () -> {
            userService.createUser(user1Dto);
        }, "Пользователь с таким email существует");
    }

    @Test
    void createUserExpectNotValidateDuplicationUser() {
        when(userRepository.save(any())).thenThrow(ConstraintViolationException.class);
        when(userRepository.findByEmailContaining(anyString())).thenReturn(Optional.empty());
        Assertions.assertThrows(ValidationException.class, () -> {
            userService.createUser(user1Dto);
        }, "Пользователь с таким email существует");
    }

    @Test
    void createUserWithTheSameEmail() {
        Assertions.assertThrows(ValidationException.class, () -> {
            userService.createUser(userErrorDto);
        }, "Пользователь с таким email существует");
    }

    @Test
    public void testUpdateUser() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("Updated John");
        userDto.setEmail("updated@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(UserMapper.toUser(userDto)));
        when(userRepository.save(any(User.class))).thenReturn(UserMapper.toUser(userDto));

        UserDto updatedUser = userService.updateUser(userDto, 1L);

        assertThat(updatedUser.getId()).isEqualTo(1L);
        assertThat(updatedUser.getName()).isEqualTo("Updated John");
        assertThat(updatedUser.getEmail()).isEqualTo("updated@example.com");
    }

    @Test
    void testUpdateUserExpectNotValidateDuplicationEmailUser() {
        UserDto userDto = new UserDto();
        userDto.setId(2L);
        userDto.setName("Updated John");
        userDto.setEmail("updated@example.com");
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(UserMapper.toUser(userDto)));
        when(userRepository.findByEmailContaining(anyString())).thenReturn(Optional.of(UserMapper.toUser(userDto)));
        when(userRepository.save(any(User.class))).thenReturn(UserMapper.toUser(userDto));

        Assertions.assertThrows(ConflictException.class, () -> {
            userService.updateUser(userDto, 1L);
        }, "Пользователь с такой почтой уже существует");
    }

    @Test
    public void testGetAllUsers() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("John");
        userDto.setEmail("john@example.com");

        when(userRepository.findAll()).thenReturn(Collections.singletonList(UserMapper.toUser(userDto)));

        List<UserDto> users = userService.getAllUsers();

        assertThat(users).hasSize(1);
        assertThat(users.get(0).getId()).isEqualTo(1L);
        assertThat(users.get(0).getName()).isEqualTo("John");
        assertThat(users.get(0).getEmail()).isEqualTo("john@example.com");
    }

    @Test
    public void testGetUserById() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("John");
        userDto.setEmail("john@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(UserMapper.toUser(userDto)));

        UserDto foundUser = userService.getUserById(1L);

        assertThat(foundUser.getId()).isEqualTo(1L);
        assertThat(foundUser.getName()).isEqualTo("John");
        assertThat(foundUser.getEmail()).isEqualTo("john@example.com");
    }

    @Test
    public void testDeleteUserById() {
        userService.deleteUserById(1L);

        Mockito.verify(userRepository, Mockito.times(1)).deleteById(1L);
    }

}