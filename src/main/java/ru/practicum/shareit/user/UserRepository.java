package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class UserRepository {
    private static UserMapper userMapper;
    private Map<Long, User> users = new HashMap<>();
    private long id;

    public Map<Long, User> getUsers() {
        return users;
    }

    public UserDto createUser(UserDto userDto){
        userDto.setId(++id);
        users.put(userDto.getId(), userMapper.toUser(userDto));
        log.info("Пользователь добавлен: {}", users.get(userDto.getId()));
        return UserMapper.toUserDto(users.get(userDto.getId()));
    }

    public UserDto updateUser(UserDto userDto, Long id){
        userDto.setId(users.get(id).getId());
        users.replace(users.get(id).getId(),users.get(id), userMapper.toUser(userDto));
        return UserMapper.toUserDto(users.get(id));
    }

    public UserDto updateUserWithoutName(UserDto userDto, Long id){
        userDto.setId(users.get(id).getId());
        userDto.setName(users.get(id).getName());
        users.replace(users.get(id).getId(),users.get(id),userMapper.toUser(userDto));
        return UserMapper.toUserDto(users.get(id));
    }

    public UserDto updateUserWithoutEmail(UserDto userDto, Long id){
        userDto.setId(users.get(id).getId());
        userDto.setEmail(users.get(id).getEmail());
        users.replace(users.get(id).getId(),users.get(id),userMapper.toUser(userDto));
        return UserMapper.toUserDto(users.get(id));
    }

    public List<UserDto> getAllUsers(){
        return users.values().stream()
                .map (f-> UserMapper.toUserDto(f))
                .collect(Collectors.toList());
    }

    public UserDto getUserById(Long id){
        return UserMapper.toUserDto(users.get(id));
    }

    public void deleteUserById(Long id){
        if (users.get(id)!= null) {
            users.remove(id);
        }
    }
}
