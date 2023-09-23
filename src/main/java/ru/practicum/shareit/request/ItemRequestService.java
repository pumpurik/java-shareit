package ru.practicum.shareit.request;

import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto createRequest(ItemRequestDto itemRequestDto, Long userId) throws NotFoundException, ValidationException;

    List<ItemRequestDto> getRequestsWithAnswers(Long userId) throws NotFoundException;

    List<ItemRequestDto> getAllRequests(Long userId, Integer from, Integer size);

    ItemRequestDto getRequestById(Long userId, Long requestId) throws NotFoundException;
}
