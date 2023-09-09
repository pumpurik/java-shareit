package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@Slf4j
@RequestMapping("/items")
public class ItemController {
    private ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto createItem(@RequestBody ItemDto itemDto, @RequestHeader(value = "X-Sharer-User-Id") Long userId)
            throws NotFoundException, ValidationException {
        return itemService.createItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody ItemDto itemDto, @PathVariable long itemId,
                              @RequestHeader(value = "X-Sharer-User-Id") Long userId) throws NotFoundException {
        return itemService.updateItem(itemDto, itemId, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable long itemId) {
        return itemService.getItemById(itemId);
    }

    @GetMapping
    public List<ItemDto> getAllItemsForOwner(@RequestHeader(value = "X-Sharer-User-Id") long userId) {
        return itemService.getAllItemsForOwner(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> getSearchItems(@RequestParam(name = "text") String text) {
        return itemService.getSearchItems(text);
    }

}
