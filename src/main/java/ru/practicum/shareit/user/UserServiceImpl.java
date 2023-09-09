package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDto createUser(UserDto userDto) throws ValidationException, ConflictException {
        validateEmail(userDto);
        validateDuplicationUser(userDto);
        return userRepository.createUser(userDto);
    }

    @Override
    public UserDto updateUser(UserDto userDto, Long id) throws ConflictException {
        if (userDto.getName() == null) {
            validateDuplicationEmailUser(userDto, id);
            return userRepository.updateUserWithoutName(userDto, id);
        } else if (userDto.getEmail() == null) {
            return userRepository.updateUserWithoutEmail(userDto, id);
        } else {
            return userRepository.updateUser(userDto, id);
        }
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.getAllUsers();
    }

    @Override
    public UserDto getUserById(Long id) {
        return userRepository.getUserById(id);
    }

    @Override
    public void deleteUserById(Long id) {
        userRepository.deleteUserById(id);
    }

    private void validateEmail(UserDto userDto) throws ValidationException {
        if (userDto.getEmail() == null || userDto.getEmail().isBlank() || !userDto.getEmail().contains("@")) {
            log.info("Ошибка почты пользователя");
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
        }
    }

    private void validateDuplicationUser(UserDto userDto) throws ConflictException {
        for (User user1 : userRepository.getUsers().values()) {
            if (user1.getEmail().equals(userDto.getEmail()) && user1.getName().equals(userDto.getName())) {
                log.info("Дубликат пользователя найден: {}", userRepository.getUsers().get(user1.getId()));
                throw new ConflictException("Такой пользователь уже существует");
            }
        }
    }

    private void validateDuplicationEmailUser(UserDto userDto, Long id) throws ConflictException {
        for (User user1 : userRepository.getUsers().values()) {
            if (user1.getEmail().equals(userDto.getEmail()) && user1.getId() != id) {
                log.info("Найдена такая же почта у другого пользователя: {}", user1);
                throw new ConflictException("Пользователь с такой почтой уже существует");
            }
        }
    }
}
