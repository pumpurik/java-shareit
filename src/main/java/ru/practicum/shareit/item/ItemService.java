package ru.practicum.shareit.item;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@Service
public interface ItemService {
    ItemDto createItem(ItemDto itemDto, Long userId) throws NotFoundException, ValidationException;

    ItemDto updateItem(ItemDto itemDto, Long itemId, Long userId) throws NotFoundException;

    ItemDto getItemById(long itemId);

    List<ItemDto> getAllItemsForOwner(long userId);

    List<ItemDto> getSearchItems(String text);
}
