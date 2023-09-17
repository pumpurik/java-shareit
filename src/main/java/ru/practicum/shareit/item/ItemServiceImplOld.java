package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.user.UserRepositoryOld;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class ItemServiceImplOld implements ItemService {
    private ItemRepositoryOld itemRepositoryOld;
    private UserRepositoryOld userRepositoryOld;

    @Autowired
    public ItemServiceImplOld(ItemRepositoryOld itemRepositoryOld, UserRepositoryOld userRepositoryOld) {
        this.itemRepositoryOld = itemRepositoryOld;
        this.userRepositoryOld = userRepositoryOld;
    }

    @Override
    public ItemDto createItem(ItemDto itemDto, Long userId) throws NotFoundException, ValidationException {
        validateOwnerOfItemExists(userId);
        validateItemFromUser(itemDto);
        return itemRepositoryOld.createItem(itemDto, userId, userRepositoryOld.getUsers().get(userId));
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, Long itemId, Long userId) throws NotFoundException {
        validateOwnerOfItemExists(userId);
        validateOwnerOfItem(userId, itemId);
        return itemRepositoryOld.updateItem(itemDto, itemId, userId);
    }

    @Override
    public ItemDtoWithBooking getItemById(Long itemId, Long userId) {
        // return itemRepositoryOld.getItemById(itemId);
        return null;
    }

    @Override
    public List<ItemDtoWithBooking> getAllItemsForOwner(Long userId) {
        return itemRepositoryOld.getAllItemsForOwner(userId);
    }

    @Override
    public List<ItemDto> getSearchItems(String text) {
        if (text == null || text.isBlank()) return Collections.emptyList();
        return itemRepositoryOld.getSearchItems(text.toLowerCase());
    }

    @Override
    public CommentDto createComment(CommentDto commentDto, Long itemId, Long userId) throws ValidationException {
        return null;
    }

    private void validateOwnerOfItemExists(Long userId) throws NotFoundException {
        if (!userRepositoryOld.getUsers().containsKey(userId)) {
            log.info("Неверный идентификатор владельца вещи: {}", userId);
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
        if (itemRepositoryOld.getItems().containsKey(userId)) {
            if (itemRepositoryOld.getItems().get(userId).containsKey(itemId)) {
                if (itemRepositoryOld.getItems().get(userId).get(itemId).getOwner() != null) {
                    if (itemRepositoryOld.getItems().get(userId).get(itemId).getOwner().getId() != userId) {
                        log.info("Пользователь c идентификатором {} не является владельцем вещи, владелец - : {}", userId,
                                itemRepositoryOld.getItems().get(userId).get(itemId).getOwner().getId());
                        throw new NotFoundException("Пользователь c идентификатором " + userId + " не является владельцем вещи");
                    }
                } else {
                    log.info("Пользователь c идентификатором {} не является владельцем вещи", userId);
                    throw new NotFoundException("Пользователь c идентификатором " + userId + " не является владельцем вещи");
                }
            } else {
                log.info("Пользователь c идентификатором {} не является владельцем вещи", userId);
                throw new NotFoundException("Пользователь c идентификатором " + userId + " не является владельцем вещи");
            }
        } else {
            log.info("Пользователь c идентификатором {} не является владельцем вещи", userId);
            throw new NotFoundException("Пользователь c идентификатором " + userId + " не является владельцем вещи");
        }
    }
}
