package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.Status;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository, BookingRepository bookingRepository, CommentRepository commentRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
    }


    @Override
    public ItemDto createItem(ItemDto itemDto, Long userId) throws NotFoundException, ValidationException {
        validateItemFromUser(itemDto);
        Optional<User> user = userRepository.findById(userId);
        validateOwnerOfItemExists(userId, user);
        itemDto.setOwner(user.get());
        return ItemMapper.toItemDto(itemRepository.save(ItemMapper.toItem(itemDto)));
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, Long itemId, Long userId) throws NotFoundException {
        Optional<User> user = userRepository.findById(userId);
        validateOwnerOfItemExists(userId, user);
        validateOwnerOfItem(userId, itemId);
        return ItemMapper.toItemDto(itemRepository.save(ItemMapper.toItemWithBlankFields(itemDto,
                itemRepository.findById(itemId).get())));
    }

    @Override
    public ItemDtoWithBooking getItemById(Long itemId, Long userId) throws NotFoundException {
        LocalDateTime time = LocalDateTime.now();
        List<CommentDto> comments = commentRepository.findAllCommentByItemId(itemId).stream()
                .map(CommentMapper::toCommentDto).collect(Collectors.toList());
        if (itemRepository.findByIdAndOwnerId(itemId, userId).isPresent()) {
            ItemDtoWithBooking item = itemRepository.findById(itemId).map(ItemMapper::toItemDtoWithBooking).orElseThrow(()
                    -> new NotFoundException("Вещь не найдена"));
            Optional<Booking> last = bookingRepository.findFirstByItemIdAndStartLessThanEqualOrderByStartDesc(itemId, time);
            Optional<Booking> next = bookingRepository.findFirstByItemIdAndStartAfterOrderByStartAsc(itemId, time);
            if (last.isPresent() && !last.get().getStatus().equals(Status.REJECTED)) {
                item.setLastBooking(BookingMapper.toBookingTwoFieldsDto(last.get(), last.get().getBooker().getId()));
            } else {
                item.setLastBooking(null);
            }
            if (next.isPresent() && !next.get().getStatus().equals(Status.REJECTED)) {
                item.setNextBooking(BookingMapper.toBookingTwoFieldsDto(next.get(), next.get().getBooker().getId()));
            } else {
                item.setNextBooking(null);
            }
            if (!comments.isEmpty()) {
                item.setComments(comments);
            }
            return item;

        } else {
            if (!comments.isEmpty()) {
                ItemDtoWithBooking item = itemRepository.findById(itemId).map(ItemMapper::toItemDtoWithBookingDtoForUser)
                        .orElseThrow(() -> new NotFoundException("Вещь не найдена"));
                item.setComments(comments);
                return item;
            } else {
                return itemRepository.findById(itemId).map(ItemMapper::toItemDtoWithBookingDtoForUser).orElseThrow(()
                        -> new NotFoundException("Вещь не найдена"));
            }
        }
    }

    @Override
    public List<ItemDtoWithBooking> getAllItemsForOwner(Long userId) {
        Map<Long, ItemDtoWithBooking> itemMap = itemRepository.findAllByOwnerId(userId).stream()
                .map(ItemMapper::toItemDtoWithBooking)
                .collect(Collectors.toMap(ItemDtoWithBooking::getId, Function.identity()));
        LocalDateTime time = LocalDateTime.now();
        for (ItemDtoWithBooking item : itemMap.values()) {
            Optional<Booking> last = bookingRepository.findFirstByItemIdAndStartLessThanEqualOrderByStartDesc(item.getId(), time);
            Optional<Booking> next = bookingRepository.findFirstByItemIdAndStartAfterOrderByStartAsc(item.getId(), time);
            List<CommentDto> comments = commentRepository.findAllCommentByItemId(item.getId()).stream()
                    .map(CommentMapper::toCommentDto).collect(Collectors.toList());
            if (last.isPresent() && !last.get().getStatus().equals(Status.REJECTED)) {
                item.setLastBooking(BookingMapper.toBookingTwoFieldsDto(last.get(), last.get().getBooker().getId()));
            } else {
                item.setLastBooking(null);
            }
            if (next.isPresent() && !next.get().getStatus().equals(Status.REJECTED)) {
                item.setNextBooking(BookingMapper.toBookingTwoFieldsDto(next.get(), next.get().getBooker().getId()));
            } else {
                item.setNextBooking(null);
            }
            if (!comments.isEmpty()) {
                item.setComments(comments);
            }
        }
        List<ItemDtoWithBooking> items = new ArrayList<>();
        itemMap.entrySet().stream()
                .forEach(f -> {
                    items.add(f.getValue());
                });
        return items;


    }

    @Override
    public List<ItemDto> getSearchItems(String text) {
        if (text == null || text.isBlank()) return Collections.emptyList();
        return itemRepository.search(text)
                .stream().map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto createComment(CommentDto commentDto, Long itemId, Long userId) throws ValidationException {
        User user = userRepository.findById(userId).get();
        Item item = itemRepository.findById(itemId).get();
        LocalDateTime time = LocalDateTime.now();
        Optional<Booking> booking = bookingRepository.findFirstByBookerIdAndItemIdAndEndLessThanEqual(userId, itemId, time);
        if (booking.isPresent() && !commentDto.getText().isEmpty()) {
            return CommentMapper.toCommentDto(commentRepository.save(CommentMapper.toComment(commentDto, item, user)));
        } else {
            throw new ValidationException("Ошибка ввода данных!");
        }
    }

    private void validateOwnerOfItemExists(Long userId, Optional<User> user) throws NotFoundException {
        if (userId == null || user.isEmpty()) {
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
        if (!itemRepository.findByIdAndOwnerId(itemId, userId).isPresent()) {
            log.info("Пользователь c идентификатором {} не является владельцем вещи", userId);
            throw new NotFoundException("Пользователь c идентификатором " + userId + " не является владельцем вещи");
        }
    }
}
