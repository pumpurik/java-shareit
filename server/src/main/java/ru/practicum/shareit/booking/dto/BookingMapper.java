package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BookingMapper {
    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem(),
                booking.getBooker(),
                booking.getStatus()
        );
    }

    public static BookingTwoFieldsDto toBookingTwoFieldsDto(Booking booking, Long bookerId) {
        return new BookingTwoFieldsDto(
                booking.getId(),
                bookerId
        );
    }

    public static Booking toBookingFromRequest(BookingDtoRequest bookingDtoRequest, User user, Status status, Item item) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        Booking booking = new Booking();
        booking.setId(bookingDtoRequest.getId());
        booking.setStart(LocalDateTime.parse(bookingDtoRequest.getStart(), formatter));
        booking.setEnd(LocalDateTime.parse(bookingDtoRequest.getEnd(), formatter));
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(status);
        return booking;
    }

    public static Booking toBooking(BookingDto bookingDto) {
        return new Booking(
                bookingDto.getStart(),
                bookingDto.getEnd(),
                bookingDto.getItem(),
                bookingDto.getBooker(),
                bookingDto.getStatus()
        );
    }
}
