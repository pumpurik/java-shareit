package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@Slf4j
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;
    private static final String X_SHARER_USER_ID = "X-Sharer-User-Id";

    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @PostMapping
    ItemRequestDto createRequest(@RequestBody ItemRequestDto itemRequestDto,
                                 @RequestHeader(value = X_SHARER_USER_ID) Long userId) throws NotFoundException, ValidationException {
        return itemRequestService.createRequest(itemRequestDto, userId);
    }

    @GetMapping
    List<ItemRequestDto> getRequestsWithAnswers(@RequestHeader(value = X_SHARER_USER_ID) Long userId) throws NotFoundException {
        return itemRequestService.getRequestsWithAnswers(userId);
    }

    @GetMapping("/all")
    List<ItemRequestDto> getAllRequests(@RequestHeader(value = X_SHARER_USER_ID) Long userId,
                                        @RequestParam Optional<Integer> from,
                                        @RequestParam Optional<Integer> size) {
        if (from.isEmpty() || size.isEmpty()) return Collections.emptyList();
        return itemRequestService.getAllRequests(userId, from.get(), size.get());
    }

    @GetMapping("/{requestId}")
    ItemRequestDto getRequestById(@RequestHeader(value = X_SHARER_USER_ID) Long userId,
                                  @PathVariable Long requestId) throws NotFoundException, ValidationException {
        return itemRequestService.getRequestById(userId, requestId);
    }

}
