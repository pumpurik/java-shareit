package ru.practicum.shareit.user;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;


public interface UserService {
    UserDto createUser(UserDto userDto) throws ValidationException, ConflictException;

    UserDto updateUser(UserDto userDto, Long id) throws ConflictException;

    List<UserDto> getAllUsers();

    UserDto getUserById(Long id) throws NotFoundException;

    void deleteUserById(Long id);
}
