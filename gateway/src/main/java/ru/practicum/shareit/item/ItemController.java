package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private static final String X_SHARER_USER_ID = "X-Sharer-User-Id";
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestBody ItemDto itemDto, @RequestHeader(X_SHARER_USER_ID) Long userId) {
        return itemClient.create(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestBody ItemDto itemDto, @PathVariable Long itemId,
                                             @RequestHeader(X_SHARER_USER_ID) Long userId) {
        return itemClient.update(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@PathVariable Long itemId,
                                              @RequestHeader(X_SHARER_USER_ID) Long userId) {
        return itemClient.getItem(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemsForOwner(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                                      @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                      @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return itemClient.getAllItemsForOwner(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getSearchItems(@RequestParam(name = "text") String text,
                                                 @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                 @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return itemClient.getSearchItems(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestBody CommentDto commentDto, @PathVariable Long itemId,
                                                @RequestHeader(X_SHARER_USER_ID) Long userId) {
        return itemClient.createComment(userId, itemId, commentDto);
    }
}
