package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDto createUser(UserDto userDto) throws ValidationException {
        validateEmail(userDto);
        try {
            return UserMapper.toUserDto(userRepository.save(UserMapper.toUser(userDto)));
        } catch (ConstraintViolationException e) {
            throw new ValidationException("пользователь с таким email существует");
        }
    }

    @Override
    public UserDto updateUser(UserDto userDto, Long id) throws ConflictException {
        validateDuplicationEmailUser(userDto, id);
        return UserMapper.toUserDto(userRepository.save(UserMapper.toUserWithBlankFields(userDto, userRepository.findById(id).get())));
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(f -> UserMapper.toUserDto(f))
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(Long id) throws NotFoundException {
        return userRepository.findById(id).map(UserMapper::toUserDto).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }

    @Override
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

    private void validateEmail(UserDto userDto) throws ValidationException {
        if (userDto.getEmail() == null || userDto.getEmail().isBlank() || !userDto.getEmail().contains("@")) {
            log.info("Ошибка почты пользователя");
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
        }
    }

    private void validateDuplicationUser(UserDto userDto) throws ConflictException {
        Optional<User> user = userRepository.findByEmailContaining(userDto.getEmail());
        if (user.isPresent()) {
            log.info("Дубликат пользователя найден: {}", user);
            throw new ConflictException("Такой пользователь уже существует");
        }
    }

    private void validateDuplicationEmailUser(UserDto userDto, Long id) throws ConflictException {
        Optional<User> user = userRepository.findByEmailContaining(userDto.getEmail());
        if (user.isPresent()) {
            if (user.get().getEmail().equals(userDto.getEmail()) && user.get().getId() != id) {
                log.info("Найдена такая же почта у другого пользователя: {}", user);
                throw new ConflictException("Пользователь с такой почтой уже существует");
            }
        }

    }
}
