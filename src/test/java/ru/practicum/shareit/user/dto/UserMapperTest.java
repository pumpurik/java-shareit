package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class UserMapperTest {
    private User user;
    private UserDto userDto;

    @BeforeEach
    public void setUp() {
        // Создаем объекты для тестирования
        user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");

        userDto = new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    @Test
    public void testToUserDto() {
        UserDto mappedUserDto = UserMapper.toUserDto(user);

        assertEquals(userDto.getId(), mappedUserDto.getId());
        assertEquals(userDto.getName(), mappedUserDto.getName());
        assertEquals(userDto.getEmail(), mappedUserDto.getEmail());
    }

    @Test
    public void testToUser() {
        User mappedUser = UserMapper.toUser(userDto);

        assertEquals(userDto.getId(), mappedUser.getId());
        assertEquals(userDto.getName(), mappedUser.getName());
        assertEquals(userDto.getEmail(), mappedUser.getEmail());
    }

    @Test
    public void testToUserWithBlankFields() {
        UserDto userDtoWithNullValues = new UserDto(
                user.getId(),
                null,
                null
        );

        User mappedUser = UserMapper.toUserWithBlankFields(userDtoWithNullValues, user);

        assertEquals(user.getId(), mappedUser.getId());
        assertEquals(mappedUser.getName(), "John Doe");
        assertEquals(mappedUser.getEmail(), "john.doe@example.com");
    }
}