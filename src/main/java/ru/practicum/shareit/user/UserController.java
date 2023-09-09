package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {
    private UserService userService;
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }
    @PostMapping
    public UserDto createUser(@RequestBody UserDto userDto) throws ValidationException, ConflictException {
        return userService.createUser(userDto);
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(@RequestBody UserDto userDto, @PathVariable long id) throws ConflictException {
        return userService.updateUser(userDto,id);
    }

    @GetMapping
    public List<UserDto> getAllUsers(){
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable long id){
        return userService.getUserById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteUserById(@PathVariable long id){
        userService.deleteUserById(id);
    }
}
