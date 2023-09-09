package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserRepository;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class ItemServiceImpl implements ItemService {
    private ItemRepository itemRepository;
    private UserRepository userRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ItemDto createItem(ItemDto itemDto, Long userId) throws NotFoundException, ValidationException {
        validateOwnerOfItemExists(userId);
        validateItemFromUser(itemDto);
        return itemRepository.createItem(itemDto, userId, userRepository.getUsers().get(userId));
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, Long itemId, Long userId) throws NotFoundException {
        validateOwnerOfItemExists(userId);
        validateOwnerOfItem(userId, itemId);
        return itemRepository.updateItem(itemDto, itemId, userId);
    }

    @Override
    public ItemDto getItemById(long itemId) {
        return itemRepository.getItemById(itemId);
    }

    @Override
    public List<ItemDto> getAllItemsForOwner (long userId){
        return itemRepository.getAllItemsForOwner(userId);
    }

    @Override
    public List<ItemDto> getSearchItems (String text) {
        if (text == null || text.isBlank()) return Collections.emptyList();
        return itemRepository.getSearchItems(text.toLowerCase());
    }
    private void validateOwnerOfItemExists(Long userId) throws NotFoundException {
        if (!userRepository.getUsers().containsKey(userId)) {
            log.info("Неверный айди владельца вещи: {}", userId);
            throw new NotFoundException("Указан неверный владелец вещи");
        }
    }

    private void validateItemFromUser(ItemDto itemDto) throws ValidationException {
        if (itemDto.getName() == null || itemDto.getName().isBlank() || itemDto.getAvailable() == null ||
                itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
            log.info("Неверное заполнение полей вещи: {}", itemDto);
            throw new ValidationException("Для добавления вещи необходимо заполнить следующие поля: имя вещи, " +
                    "доступность вещи и описание вещи");
        }
    }

    private void validateOwnerOfItem(long userId, long itemId) throws NotFoundException {
        if (itemRepository.getItems().containsKey(userId)) {
            if (itemRepository.getItems().get(userId).containsKey(itemId)) {
                if (itemRepository.getItems().get(userId).get(itemId).getOwner() != null) {
                    if (itemRepository.getItems().get(userId).get(itemId).getOwner().getId() != userId) {
                        log.info("Пользователь не является владельцем вещи: {}", userId);
                        throw new NotFoundException("Пользователь не является владельцем вещи");
                    }
                } else {
                    log.info("Пользователь не является владельцем вещи: {}", userId);
                    throw new NotFoundException("Пользователь не является владельцем вещи");
                }
            } else {
                log.info("Пользователь не является владельцем вещи: {}", userId);
                throw new NotFoundException("Пользователь не является владельцем вещи");
            }
        } else {
            log.info("Пользователь не является владельцем вещи: {}", userId);
            throw new NotFoundException("Пользователь не является владельцем вещи");
        }
    }
}
