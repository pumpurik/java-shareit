package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@Slf4j
@RequestMapping("/items")
public class ItemController {
    private static final String X_SHARER_USER_ID = "X-Sharer-User-Id";
    private ItemService itemService;

    @Autowired
    public ItemController(@Qualifier("itemServiceImpl") ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto createItem(@RequestBody ItemDto itemDto, @RequestHeader(value = X_SHARER_USER_ID) Long userId)
            throws NotFoundException, ValidationException {
        return itemService.createItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody ItemDto itemDto, @PathVariable Long itemId,
                              @RequestHeader(value = X_SHARER_USER_ID) Long userId) throws NotFoundException {
        return itemService.updateItem(itemDto, itemId, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDtoWithBooking getItemById(@PathVariable Long itemId,
                                          @RequestHeader(value = X_SHARER_USER_ID) Long userId) throws NotFoundException {
        return itemService.getItemById(itemId, userId);
    }

    @GetMapping
    public List<ItemDtoWithBooking> getAllItemsForOwner(@RequestHeader(value = X_SHARER_USER_ID) Long userId) {
        return itemService.getAllItemsForOwner(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> getSearchItems(@RequestParam(name = "text") String text) {
        return itemService.getSearchItems(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestBody CommentDto commentDto, @PathVariable Long itemId,
                                    @RequestHeader(value = X_SHARER_USER_ID) Long userId) throws ValidationException {
        return itemService.createComment(commentDto, itemId, userId);
    }

}
