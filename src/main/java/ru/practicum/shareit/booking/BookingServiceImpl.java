package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.*;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public BookingServiceImpl(BookingRepository bookingRepository, ItemRepository itemRepository,
                              UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public BookingDto createBooking(BookingDtoRequest bookingDtoRequest, Long userId) throws Exception {
        User userById = userRepository.findById(userId).orElseThrow(() -> {
            log.info("Пользователь {} не найден", userId);
            return new NotFoundException(String.format("Пользователь %s не найден", userId));
        });
        Item itemById = itemRepository.findById(bookingDtoRequest.getItemId()).orElseThrow(() -> {
            log.info("Вещь по айди {} не найдена", bookingDtoRequest.getId());
            return new NotFoundException(String.format("Вещь по айди %s не найдена!", bookingDtoRequest.getId()));
        });
        validateItemForAvailable(itemById);
        validateStartAndEndOfBooking(bookingDtoRequest);
        validateOwnerIdNotEqualsUserId(itemById, userById);
        return BookingMapper.toBookingDto(bookingRepository.save(BookingMapper.toBookingFromRequest(bookingDtoRequest, userById, Status.WAITING, itemById)));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public BookingDto approveBooking(Long bookingId, boolean approved, Long userId) throws NotFoundException, ValidationException {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> {
            log.info("Аренды не существует: {}", bookingId);
            return new NotFoundException(String.format("Аренда не найдена по айди %s", bookingId));
        });
        Item item = itemRepository.findById(booking.getItem().getId()).orElseThrow(() -> {
            log.info("Вещь по айди {} не найдена", booking.getItem().getId());
            return new NotFoundException(String.format("Вещь по айди %s не найдена!", booking.getItem().getId()));
        });
        validateOwnerOfItem(userId, item);
        validateBookingBeforeApprove(booking, approved);
        if (approved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getBookingById(Long bookingId, Long userId) throws NotFoundException {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> {
            log.info("Аренды не существует: {}", bookingId);
            return new NotFoundException("Аренда не найдена!");
        });
        Item item = itemRepository.findById(booking.getItem().getId()).orElseThrow(() -> {
            log.info("Вещь по айди {} не найдена", booking.getItem().getId());
            return new NotFoundException(String.format("Вещь по айди %s не найдена!", booking.getItem().getId()));
        });
        validateOwnerIdBookingOrOwnerIdItemForBooking(userId, booking, item);
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getBookingsByState(State state, Long userId) throws Exception {
        User userById = userRepository.findById(userId).orElseThrow(() -> {
            log.info("Пользователь {} не найден", userId);
            return new NotFoundException(String.format("Пользователь %s не найден", userId));
        });
        List<BookingDto> bookings = null;
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAll(Sort.by(Sort.Direction.DESC, "start"))
                        .stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByStartBeforeAndEndAfter(LocalDateTime.now(), LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "start"))
                        .stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
                break;
            case PAST:
                bookings = bookingRepository.findAllByEndBefore(LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "start"))
                        .stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByStartAfter(LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "start"))
                        .stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
                break;
        }
        if (state.toString().equals(Status.REJECTED.toString()) || state.toString().equals(Status.WAITING.toString())) {
            bookings = bookingRepository
                    .findAllByStatusAndBooker(Status.valueOf(state.toString()), userById, Sort.by(Sort.Direction.DESC, "start"))
                    .stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
        }
        return bookings;
    }

    @Override
    public List<BookingDto> getBookingsByOwnerOfItems(State state, Long userId) throws Exception {
        User userById = userRepository.findById(userId).orElseThrow(() -> {
            log.info("Пользователь {} не найден", userId);
            return new NotFoundException(String.format("Пользователь %s не найден", userId));
        });
        List<BookingDto> bookings = null;
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByItemOwner(userById, Sort.by(Sort.Direction.DESC, "start"))
                        .stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByItemOwnerAndStartBeforeAndEndAfter(userById, LocalDateTime.now(), LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "start"))
                        .stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
                break;
            case PAST:
                bookings = bookingRepository.findAllByItemOwnerAndEndBefore(userById, LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "start"))
                        .stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByItemOwnerAndStartAfter(userById, LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "start"))
                        .stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
                break;
        }
        if (state.toString().equals((Status.REJECTED).toString()) || state.toString().equals((Status.WAITING).toString())) {
            bookings = bookingRepository
                    .findAllByStatusAndItemOwner(Status.valueOf(state.toString()), userById, Sort.by(Sort.Direction.DESC, "start"))
                    .stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
        }
        return bookings;
    }

    private void validateItemForAvailable(Item item) throws ValidationException {
        if (!item.isAvailable()) {
            log.info("Вещь недоступна: {}", item);
            throw new ValidationException("Вещь недоступна!");
        }
    }

    private void validateStartAndEndOfBooking(BookingDtoRequest bookingDtoRequest) throws ValidationException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        if (bookingDtoRequest.getStart() == null || bookingDtoRequest.getEnd() == null
                || bookingDtoRequest.getStart().equals(bookingDtoRequest.getEnd())
                || bookingDtoRequest.getStart().equals(LocalDateTime.now().toString())
                || bookingDtoRequest.getEnd().equals(LocalDateTime.now().toString())
                || LocalDateTime.parse(bookingDtoRequest.getStart(), formatter).isBefore(LocalDateTime.now())
                || LocalDateTime.parse(bookingDtoRequest.getEnd(), formatter)
                .isBefore(LocalDateTime.parse(bookingDtoRequest.getStart(), formatter))) {
            log.info("Некорректное время бронирования : {}, {}", bookingDtoRequest.getStart(), bookingDtoRequest.getEnd());
            throw new ValidationException("Некорректное время бронирования");
        }
    }

    private void validateOwnerOfItem(Long userId, Item item) throws NotFoundException {
        if (item.getOwner().getId() != userId) {
            log.info("Пользователь не найден или не является владельцем вещи: {}", userId);
            throw new NotFoundException(String.format("Владелец вещи не найден по айди %s!", userId));
        }
    }

    private void validateBookingBeforeApprove(Booking booking, boolean approved) throws ValidationException {
        if ((approved && booking.getStatus() == Status.APPROVED)
                || (!approved && booking.getStatus() == Status.REJECTED)) {
            log.info("Статус уже изменен: {}", booking.getStatus());
            throw new ValidationException("Статус уже изменен!");
        }
    }

    private void validateOwnerIdNotEqualsUserId(Item item, User user) throws NotFoundException {
        if (item.getOwner().getId() == user.getId()) {
            log.info("Пользователь с айди {} не может забронировать свою вещь", user.getId());
            throw new NotFoundException(String.format("Пользователь с айди %s не может забронировать свою вещь", user.getId()));
        }
    }

    private void validateOwnerIdBookingOrOwnerIdItemForBooking(Long userId, Booking booking, Item item) throws NotFoundException {
        if (booking.getBooker().getId() != userId
                && item.getOwner().getId() != userId) {
            log.info("Пользователь не является ни владельцем вещи, ни владельцем аренды: {}", userId);
            throw new NotFoundException(String.format("Владелец вещи не найден по айди %s!", userId));
        }
    }
}
