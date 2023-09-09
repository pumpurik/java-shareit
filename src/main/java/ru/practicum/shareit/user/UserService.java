package ru.practicum.shareit.user;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Service
public interface UserService {
    UserDto createUser(UserDto userDto) throws ValidationException, ConflictException;

    UserDto updateUser(UserDto userDto, Long id) throws ConflictException;

    List<UserDto> getAllUsers();

    UserDto getUserById(Long id);

    void deleteUserById(Long id);
}
