package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemRequestRepository requestRepository;

    public ItemRequestServiceImpl(ItemRepository itemRepository, UserRepository userRepository, ItemRequestRepository requestRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.requestRepository = requestRepository;
    }

    @Override
    public ItemRequestDto createRequest(ItemRequestDto itemRequestDto, Long userId) throws NotFoundException, ValidationException {
        User user = userRepository.findById(userId).orElseThrow(() -> {
            log.info("Пользователь {} не найден", userId);
            return new NotFoundException(String.format("Пользователь не найден %s!", userId));
        });
        if (itemRequestDto.getDescription() == null) {
            log.info("Описание не заполнено");
            throw new ValidationException("Описание не заполнено!");
        }
        return ItemRequestMapper.toItemRequestDto(requestRepository.save(ItemRequestMapper.toItemRequest(itemRequestDto, user)));
    }

    @Override
    public List<ItemRequestDto> getRequestsWithAnswers(Long userId) throws NotFoundException {
        userRepository.findById(userId).orElseThrow(() -> {
            log.info("Пользователь {} не найден", userId);
            return new NotFoundException(String.format("Пользователь не найден %s!", userId));
        });
        List<ItemRequestDto> itemRequests = requestRepository.findAllByRequestorId(userId).stream()
                .map(ItemRequestMapper::toItemRequestWithItemsDto)
                .collect(Collectors.toList());
        List<ItemDto> items = itemRepository.findAll().stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        for (ItemRequestDto itemRequestDto : itemRequests) {
            for (ItemDto itemDto : items) {
                if (itemDto.getRequestId() != null && itemRequestDto.getId() == itemDto.getRequestId()) {
                    itemRequestDto.setItems(List.of(itemDto));
                }
            }
        }
        return itemRequests;
    }

    @Override
    public List<ItemRequestDto> getAllRequests(Long userId, Integer from, Integer size) {
        Sort sortByCreated = Sort.by(Sort.Direction.ASC, "created");
        List<ItemRequestDto> itemRequests = requestRepository.findAllByRequestorIdNot(userId, PageRequest.of(from, size, sortByCreated)).getContent().stream()
                .map(ItemRequestMapper::toItemRequestDto).collect(Collectors.toList());
        List<ItemDto> items = itemRepository.findAll().stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        for (ItemRequestDto itemRequestDto : itemRequests) {
            for (ItemDto itemDto : items) {
                if (itemDto.getRequestId() != null && itemRequestDto.getId() == itemDto.getRequestId()) {
                    itemRequestDto.setItems(List.of(itemDto));
                }
            }
        }
        return itemRequests;
    }

    @Override
    public ItemRequestDto getRequestById(Long userId, Long requestId) throws NotFoundException {
        userRepository.findById(userId).orElseThrow(() -> {
            log.info("Пользователь {} не найден", userId);
            return new NotFoundException(String.format("Пользователь не найден!", userId));
        });
        requestRepository.findById(requestId).orElseThrow(() -> {
            log.info("Запрос на вещь {} не найден", requestId);
            return new NotFoundException(String.format("Запрос на вещь не найден!", requestId));
        });
        ItemRequestDto itemRequest = ItemRequestMapper.toItemRequestWithItemsDto(requestRepository.findById(requestId).get());
        List<ItemDto> items = itemRepository.findAll().stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        for (ItemDto itemDto : items) {
            if (itemDto.getRequestId() != null && itemRequest.getId() == itemDto.getRequestId()) {
                itemRequest.setItems(List.of(itemDto));
            }
        }
        return itemRequest;
    }
}

