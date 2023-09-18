package ru.practicum.shareit.item;

import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;

import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto, Long userId) throws NotFoundException, ValidationException;

    ItemDto updateItem(ItemDto itemDto, Long itemId, Long userId) throws NotFoundException;

    ItemDtoWithBooking getItemById(Long itemId, Long userId) throws NotFoundException;

    List<ItemDtoWithBooking> getAllItemsForOwner(Long userId);

    List<ItemDto> getSearchItems(String text);

    CommentDto createComment(CommentDto commentDto, Long itemId, Long userId) throws ValidationException;
}
