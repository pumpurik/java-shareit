package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class BookingMapperTest {

    private Booking booking;
    private BookingDto bookingDto;
    private BookingDtoRequest bookingDtoRequest;
    private User user;
    private Item item;

    @BeforeEach
    public void setUp() {
        // Создаем заглушки (mock objects) для зависимостей
        user = mock(User.class);
        item = mock(Item.class);

        // Создаем объекты для тестирования
        booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusHours(2));
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(Status.APPROVED);

        bookingDto = new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem(),
                booking.getBooker(),
                booking.getStatus()
        );

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        bookingDtoRequest = new BookingDtoRequest();
        bookingDtoRequest.setId(1L);
        bookingDtoRequest.setStart(LocalDateTime.now().format(formatter));
        bookingDtoRequest.setEnd(LocalDateTime.now().plusHours(2).format(formatter));
    }

    @Test
    public void testToBookingDto() {
        BookingDto mappedBookingDto = BookingMapper.toBookingDto(booking);

        assertEquals(bookingDto.getId(), mappedBookingDto.getId());
        assertEquals(bookingDto.getStart(), mappedBookingDto.getStart());
        assertEquals(bookingDto.getEnd(), mappedBookingDto.getEnd());
        assertEquals(bookingDto.getItem(), mappedBookingDto.getItem());
        assertEquals(bookingDto.getBooker(), mappedBookingDto.getBooker());
        assertEquals(bookingDto.getStatus(), mappedBookingDto.getStatus());
    }

    @Test
    public void testToBookingTwoFieldsDto() {
        Long bookerId = 2L;
        BookingTwoFieldsDto mappedBookingTwoFieldsDto = BookingMapper.toBookingTwoFieldsDto(booking, bookerId);

        assertEquals(booking.getId(), mappedBookingTwoFieldsDto.getId());
        assertEquals(bookerId, mappedBookingTwoFieldsDto.getBookerId());
    }

    @Test
    public void testToBookingFromRequest() {
        Booking mappedBooking = BookingMapper.toBookingFromRequest(bookingDtoRequest, user, Status.APPROVED, item);

        assertEquals(bookingDtoRequest.getId(), mappedBooking.getId());
        assertEquals(bookingDtoRequest.getStart(), mappedBooking.getStart().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
        assertEquals(bookingDtoRequest.getEnd(), mappedBooking.getEnd().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
        assertEquals(item, mappedBooking.getItem());
        assertEquals(user, mappedBooking.getBooker());
        assertEquals(Status.APPROVED, mappedBooking.getStatus());
    }

    @Test
    public void testToBooking() {
        Booking mappedBooking = BookingMapper.toBooking(bookingDto);

        assertEquals(bookingDto.getStart(), mappedBooking.getStart());
        assertEquals(bookingDto.getEnd(), mappedBooking.getEnd());
        assertEquals(bookingDto.getItem(), mappedBooking.getItem());
        assertEquals(bookingDto.getBooker(), mappedBooking.getBooker());
        assertEquals(bookingDto.getStatus(), mappedBooking.getStatus());
    }
}