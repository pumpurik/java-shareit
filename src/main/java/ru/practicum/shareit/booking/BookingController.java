package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.State;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;

import java.util.List;
import java.util.Optional;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@Slf4j
@RequestMapping(path = "/bookings")
public class BookingController {
    private BookingService bookingService;
    private static final String X_SHARER_USER_ID = "X-Sharer-User-Id";

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingDto createBooking(@RequestBody BookingDtoRequest bookingDtoRequest,
                                    @RequestHeader(value = X_SHARER_USER_ID) Long userId) throws Exception {
        return bookingService.createBooking(bookingDtoRequest, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(@PathVariable Long bookingId, @RequestParam boolean approved,
                                     @RequestHeader(value = X_SHARER_USER_ID) Long userId) throws NotFoundException, ValidationException {
        return bookingService.approveBooking(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@PathVariable Long bookingId,
                                     @RequestHeader(value = X_SHARER_USER_ID) Long userId) throws NotFoundException {
        return bookingService.getBookingById(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> getBookingsByState(@RequestParam(defaultValue = "ALL") String state,
                                               @RequestHeader(value = X_SHARER_USER_ID) Long userId,
                                               @RequestParam Optional<Integer> from,
                                               @RequestParam Optional<Integer> size
    ) throws Exception {
        try {
            State validState = State.valueOf(state);
            if (from.isEmpty() || size.isEmpty()) return bookingService.getBookingsByState(validState, userId);
            if (from.get() < 0 || size.get() < 0) throw new ValidationException();
            return bookingService.getBookingsByState(validState, userId, from.get(), size.get());
        } catch (IllegalArgumentException e) {
            throw new ValidationException(String.format("Unknown state: %s", state));
        }
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingsByOwnerOfItems(@RequestParam(defaultValue = "ALL") String state,
                                                      @RequestHeader(value = X_SHARER_USER_ID) Long userId,
                                                      @RequestParam Optional<Integer> from,
                                                      @RequestParam Optional<Integer> size
    ) throws Exception {
        try {
            State validState = State.valueOf(state);
            if (from.isEmpty() || size.isEmpty()) return bookingService.getBookingsByOwnerOfItems(validState, userId);
            if (from.get() < 0 || size.get() < 0) throw new ValidationException();
            return bookingService.getBookingsByOwnerOfItems(validState, userId, from.get(), size.get());
        } catch (IllegalArgumentException e) {
            throw new ValidationException(String.format("Unknown state: %s", state));
        }

    }
}
