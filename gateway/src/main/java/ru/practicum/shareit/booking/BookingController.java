package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

import javax.validation.Valid;
import javax.validation.ValidationException;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private static final String X_SHARER_USER_ID = "X-Sharer-User-Id";
    private final BookingClient bookingClient;

    @GetMapping
    public ResponseEntity<Object> getBookings(@RequestHeader(X_SHARER_USER_ID) long userId,
                                              @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                              @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        try {
            BookingState state = BookingState.from(stateParam)
                    .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
            log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
            return bookingClient.getBookings(userId, state, from, size);
        } catch (IllegalArgumentException e) {
            throw new ValidationException(String.format("Unknown state: %s", stateParam));
        }
    }

    @PostMapping
    public ResponseEntity<Object> bookItem(@RequestHeader(X_SHARER_USER_ID) long userId,
                                           @RequestBody @Valid BookItemRequestDto requestDto) {
        log.info("Creating booking {}, userId={}", requestDto, userId);
        return bookingClient.bookItem(userId, requestDto);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader(X_SHARER_USER_ID) long userId,
                                             @PathVariable Long bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(@PathVariable Long bookingId, @RequestParam boolean approved,
                                                 @RequestHeader(X_SHARER_USER_ID) Long userId) {
        log.info("Patch approve booking {}, userId={}, approved={} ", bookingId, userId, approved);
        return bookingClient.approveBooking(userId, bookingId, approved);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsByOwnerOfItems(@RequestHeader(X_SHARER_USER_ID) long userId,
                                                            @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                                            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Get booking with state and owner {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        try {
            BookingState state = BookingState.from(stateParam)
                    .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
            return bookingClient.getBookingsByOwner(userId, state, from, size);
        } catch (IllegalArgumentException e) {
            throw new ValidationException(String.format("Unknown state: %s", stateParam));
        }
    }
}