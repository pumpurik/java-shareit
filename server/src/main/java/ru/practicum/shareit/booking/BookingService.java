package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.State;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;

import java.util.List;

public interface BookingService {
    BookingDto createBooking(BookingDtoRequest bookingDtoRequest, Long userId) throws Exception;

    BookingDto approveBooking(Long bookingId, boolean approved, Long userId) throws NotFoundException, ValidationException;

    BookingDto getBookingById(Long bookingId, Long userId) throws NotFoundException;

    List<BookingDto> getBookingsByState(State state, Long userId, Integer from, Integer size) throws Exception;

    List<BookingDto> getBookingsByState(State state, Long userId) throws Exception;

    List<BookingDto> getBookingsByOwnerOfItems(State state, Long userId, Integer from, Integer size) throws Exception;

    List<BookingDto> getBookingsByOwnerOfItems(State state, Long userId) throws Exception;
}
