package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.State;
import ru.practicum.shareit.booking.dto.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class BookingServiceImplTest {
    @InjectMocks
    private BookingServiceImpl bookingService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    private BookingDto firstBookingDto;
    private BookingDto toBookingDto;
    private Booking bookingOne;
    private Booking bookingTwo;
    private Item itemOne;
    private Item itemTwo;
    private User userOne;
    private User userTwo;
    private ItemRequest itemRequest;
    private BookingDtoRequest bookingDtoRequest;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private final static String startTimeOne = "2023-06-21T01:29:22";
    private final static String endTimeOne = "2023-10-22T02:30:22";
    private final static String startTimeTwo = "2023-08-21T01:29:22";
    private final static String endTimeTwo = "2023-08-30T02:30:22";
    private final static String nowDate = "2023-08-24T01:29:22";
    private final static String nowDateTwo = "2023-07-24T01:29:22";

    @BeforeEach
    void init() {
        bookingDtoRequest = new BookingDtoRequest(1, startTimeOne, endTimeOne, 2L);


        itemRequest = new ItemRequest();
        userOne = new User(1, "name", "email");
        userTwo = new User(2, "name2", "email2");
        itemOne = new Item(1, "item1", "description1", true, userTwo, itemRequest);
        itemTwo = new Item(2, "item2", "description2", true, userOne, itemRequest);
        bookingOne = new Booking(1, LocalDateTime.parse(startTimeOne, formatter), LocalDateTime.parse(endTimeOne, formatter), itemTwo, userOne, Status.WAITING);
        bookingTwo = new Booking(2, LocalDateTime.parse(startTimeTwo, formatter), LocalDateTime.parse(endTimeTwo, formatter), itemOne, userTwo, Status.APPROVED);
        firstBookingDto = new BookingDto(1, LocalDateTime.parse(startTimeOne, formatter),
                LocalDateTime.parse(endTimeOne, formatter), itemTwo, userOne, Status.WAITING);
        toBookingDto = new BookingDto(2, LocalDateTime.parse(startTimeTwo, formatter),
                LocalDateTime.parse(endTimeTwo, formatter), itemOne, userTwo, Status.APPROVED);


    }

    @Test
    void createBooking() throws Exception {

        bookingDtoRequest.setStart(formatter.format(LocalDateTime.now().plusDays(1)));
        bookingDtoRequest.setEnd(formatter.format(LocalDateTime.now().plusDays(2)));
        when(bookingRepository.save(any())).thenReturn(bookingTwo);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(itemTwo));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userTwo));
        BookingDto booking = bookingService.createBooking(bookingDtoRequest, 1L);

        assertThat(booking.getStatus());
        assertThat(booking.getId()).isEqualTo(2);
        assertThat(booking.getStart()).isEqualTo(LocalDateTime.parse(startTimeTwo, formatter));
        assertThat(booking.getEnd()).isEqualTo(LocalDateTime.parse(endTimeTwo, formatter));
        assertThat(booking.getItem()).isEqualTo(itemOne);
        assertThat(booking.getBooker()).isEqualTo(userTwo);
    }

    @Test
    void createBookingExpectNotFoundExceptionUser() {
        Assertions.assertThrows(NotFoundException.class, () -> {
            bookingService.createBooking(bookingDtoRequest, 1L);
        }, String.format("Пользователь %s не найден", 1L));
    }

    @Test
    void createBookingExpectNotFoundExceptionItem() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userTwo));
        Assertions.assertThrows(NotFoundException.class, () -> {
            bookingService.createBooking(bookingDtoRequest, 1L);
        }, String.format("Вещь по айди %s не найдена!", bookingDtoRequest.getId()));
    }

    @Test
    void createBookingExpectNotValidateItemForAvailable() {
        bookingDtoRequest.setStart(formatter.format(LocalDateTime.now().plusDays(1)));
        bookingDtoRequest.setEnd(formatter.format(LocalDateTime.now().plusDays(2)));
        itemTwo.setAvailable(false);
        when(bookingRepository.save(any())).thenReturn(bookingTwo);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(itemTwo));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userTwo));
        Assertions.assertThrows(ValidationException.class, () -> {
            BookingDto booking = bookingService.createBooking(bookingDtoRequest, 1L);
        }, "Вещь недоступна!");

    }

    @Test
    void createBookingExpectNotValidateStartAndEndOfBooking() {
        when(bookingRepository.save(any())).thenReturn(bookingTwo);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(itemTwo));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userTwo));
        Assertions.assertThrows(ValidationException.class, () -> {
            BookingDto booking = bookingService.createBooking(bookingDtoRequest, 1L);
        }, "Некорректное время бронирования");

    }

    @Test
    void createBookingExpectNotValidateOwnerIdNotEqualsUserId() {
        bookingDtoRequest.setStart(formatter.format(LocalDateTime.now().plusDays(1)));
        bookingDtoRequest.setEnd(formatter.format(LocalDateTime.now().plusDays(2)));
        itemTwo.setOwner(userOne);
        when(bookingRepository.save(any())).thenReturn(bookingTwo);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(itemTwo));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userOne));
        Assertions.assertThrows(NotFoundException.class, () -> {
            BookingDto booking = bookingService.createBooking(bookingDtoRequest, 1L);
        }, String.format("Пользователь с айди %s не может забронировать свою вещь", 1L));

    }

    @Test
    void approveTrueBooking() throws ValidationException, NotFoundException {
        when(bookingRepository.findById(any())).thenReturn(Optional.of(bookingOne));
        when(bookingRepository.save(any())).thenReturn(bookingOne);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(itemTwo));
        BookingDto booking = bookingService.approveBooking(1L, true, 1L);

        assertThat(booking.getStatus()).isEqualTo(Status.APPROVED);
        assertThat(booking.getId()).isEqualTo(1);
        assertThat(booking.getStart()).isEqualTo(LocalDateTime.parse(startTimeOne, formatter));
        assertThat(booking.getEnd()).isEqualTo(LocalDateTime.parse(endTimeOne, formatter));
        assertThat(booking.getItem()).isEqualTo(itemTwo);
        assertThat(booking.getBooker()).isEqualTo(userOne);
    }

    @Test
    void approveTrueBookingExpectNotFoundExceptionBooking() {
        when(bookingRepository.findById(any())).thenReturn(Optional.of(bookingOne));
        when(bookingRepository.save(any())).thenReturn(bookingOne);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(itemOne));
        Assertions.assertThrows(NotFoundException.class, () -> {
            bookingService.approveBooking(1L, true, 1L);
        }, String.format("Аренда не найдена по айди %s", 1L));
    }

    @Test
    void approveBookingExpectNotValidateOwnerOfItem() {
        Assertions.assertThrows(NotFoundException.class, () -> {
            bookingService.approveBooking(1L, true, 1L);
        }, String.format("Владелец вещи не найден по айди %s!", 1L));
    }

    @Test
    void approveBookingExpectNotValidateBookingBeforeApprove() {
        bookingOne.setStatus(Status.APPROVED);
        when(bookingRepository.findById(any())).thenReturn(Optional.of(bookingOne));
        when(bookingRepository.save(any())).thenReturn(bookingOne);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(itemTwo));
        Assertions.assertThrows(ValidationException.class, () -> {
            bookingService.approveBooking(1L, true, 1L);
        }, String.format("Статус уже изменен!"));
    }

    @Test
    void approveTrueBookingExpectNotFoundExceptionItem() {
        when(bookingRepository.findById(any())).thenReturn(Optional.of(bookingOne));
        Assertions.assertThrows(NotFoundException.class, () -> {
            bookingService.approveBooking(1L, true, 1L);
        }, String.format("Вещь по айди %s не найдена!", bookingOne.getItem().getId()));
    }

    @Test
    void approveFalseBooking() throws ValidationException, NotFoundException {
        when(bookingRepository.findById(any())).thenReturn(Optional.of(bookingOne));
        when(bookingRepository.save(any())).thenReturn(bookingOne);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(itemTwo));
        BookingDto booking = bookingService.approveBooking(1L, false, 1L);

        assertThat(booking.getStatus()).isEqualTo(Status.REJECTED);
        assertThat(booking.getId()).isEqualTo(1);
        assertThat(booking.getStart()).isEqualTo(LocalDateTime.parse(startTimeOne, formatter));
        assertThat(booking.getEnd()).isEqualTo(LocalDateTime.parse(endTimeOne, formatter));
        assertThat(booking.getItem()).isEqualTo(itemTwo);
        assertThat(booking.getBooker()).isEqualTo(userOne);
    }

    @Test
    void getBookingById() throws NotFoundException {
        when(bookingRepository.findById(any())).thenReturn(Optional.of(bookingOne));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(itemOne));
        BookingDto booking = bookingService.getBookingById(1L, 1L);
        assertThat(booking.getStatus()).isEqualTo(Status.WAITING);
        assertThat(booking.getId()).isEqualTo(1);
        assertThat(booking.getStart()).isEqualTo(LocalDateTime.parse(startTimeOne, formatter));
        assertThat(booking.getEnd()).isEqualTo(LocalDateTime.parse(endTimeOne, formatter));
        assertThat(booking.getItem()).isEqualTo(itemTwo);
        assertThat(booking.getBooker()).isEqualTo(userOne);
    }

    @Test
    void getBookingByIdExpectNotValidateOwnerIdBookingOrOwnerIdItemForBooking() {
        bookingOne.setBooker(userTwo);
        when(bookingRepository.findById(any())).thenReturn(Optional.of(bookingOne));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(itemOne));
        Assertions.assertThrows(NotFoundException.class, () -> {
            BookingDto booking = bookingService.getBookingById(1L, 1L);
        }, String.format("Владелец вещи не найден по айди %s!", 1L));

    }

    @Test
    void getBookingByIdExpectNotFoundExceptionBooking() {
        Assertions.assertThrows(NotFoundException.class, () -> {
            bookingService.getBookingById(1L, 1L);
        }, String.format("Аренда не найдена по айди %s", 1L));
    }

    @Test
    void getBookingByIdExpectNotFoundExceptionItem() {
        when(bookingRepository.findById(any())).thenReturn(Optional.of(bookingOne));
        Assertions.assertThrows(NotFoundException.class, () -> {
            bookingService.getBookingById(1L, 1L);
        }, String.format("Вещь по айди %s не найдена!", bookingOne.getItem().getId()));
    }

    @Test
    void getBookingsByStateAll() throws Exception {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userOne));
        when(bookingRepository.findAll((Sort) any())).thenReturn(List.of(bookingOne, bookingTwo));
        List<BookingDto> bookings = bookingService.getBookingsByState(State.ALL, 1L);
        assertThat(bookings.size()).isEqualTo(2);
        assertThat(bookings).contains(firstBookingDto);
        assertThat(bookings).contains(toBookingDto);
        assertThat(bookings.get(0)).isEqualTo(firstBookingDto);

    }


    @Test
    void getBookingsByStateCURRENT() throws Exception {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userOne));
        when(bookingRepository.findAllByStartBeforeAndEndAfter(any(), any(), (Sort) any())).thenReturn(List.of(bookingOne));
        List<BookingDto> bookings = bookingService.getBookingsByState(State.CURRENT, 1L);
        assertThat(bookings.size()).isEqualTo(1);
        assertThat(bookings).contains(firstBookingDto);
        assertThat(bookings.get(0)).isEqualTo(firstBookingDto);

    }

    @Test
    void getBookingsByStatePAST() throws Exception {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userOne));
        when(bookingRepository.findAllByEndBefore(any(), (Sort) any())).thenReturn(List.of(bookingTwo));
        List<BookingDto> bookings = bookingService.getBookingsByState(State.PAST, 1L);
        assertThat(bookings.size()).isEqualTo(1);
        assertThat(bookings).contains(toBookingDto);
        assertThat(bookings.get(0)).isEqualTo(toBookingDto);

    }

    @Test
    void getBookingsByStateFUTURE() throws Exception {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userOne));
        when(bookingRepository.findAllByStartAfter(any(), (Sort) any())).thenReturn(List.of(bookingOne));
        List<BookingDto> bookings = bookingService.getBookingsByState(State.FUTURE, 1L);
        assertThat(bookings.size()).isEqualTo(1);
        assertThat(bookings).contains(firstBookingDto);
        assertThat(bookings.get(0)).isEqualTo(firstBookingDto);
    }

    @Test
    void getBookingsByStateRejectedOrWaiting() throws Exception {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userOne));
        when(bookingRepository.findAllByStatusAndBooker(any(), any(), (Sort) any())).thenReturn(List.of(bookingOne));
        List<BookingDto> bookings = bookingService.getBookingsByState(State.REJECTED, 1L);
        assertThat(bookings.size()).isEqualTo(1);
        assertThat(bookings).contains(firstBookingDto);
        assertThat(bookings.get(0)).isEqualTo(firstBookingDto);
    }

    @Test
    void getBookingsByStatePageableAll() throws Exception {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userOne));
        when(bookingRepository.findAllByBooker(any(), (Pageable) any())).thenReturn(new PageImpl<>(List.of(bookingOne, bookingTwo)));
        List<BookingDto> bookings = bookingService.getBookingsByState(State.ALL, 1L, 0, 2);
        assertThat(bookings.size()).isEqualTo(2);
        assertThat(bookings).contains(firstBookingDto);
        assertThat(bookings).contains(toBookingDto);
        assertThat(bookings.get(0)).isEqualTo(firstBookingDto);

    }

    @Test
    void getBookingsByStateAllxpectNotFoundExceptionBooking() {
        Assertions.assertThrows(NotFoundException.class, () -> {
            bookingService.getBookingsByState(State.ALL, 1L);
        }, String.format("Аренда не найдена по айди %s", 1L));
    }

    @Test
    void getBookingsByStatePageableAllxpectNotFoundExceptionBooking() {
        Assertions.assertThrows(NotFoundException.class, () -> {
            bookingService.getBookingsByState(State.ALL, 1L, 1, 1);
        }, String.format("Аренда не найдена по айди %s", 1L));
    }

    @Test
    void getBookingsByStatePageableCURRENT() throws Exception {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userOne));
        when(bookingRepository.findAllByStartBeforeAndEndAfterAndBooker(any(), any(), any(), (Pageable) any())).thenReturn(List.of(bookingOne));
        List<BookingDto> bookings = bookingService.getBookingsByState(State.CURRENT, 1L, 0, 2);
        assertThat(bookings.size()).isEqualTo(1);
        assertThat(bookings).contains(firstBookingDto);
        assertThat(bookings.get(0)).isEqualTo(firstBookingDto);

    }

    @Test
    void getBookingsByStatePageablePAST() throws Exception {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userOne));
        when(bookingRepository.findAllByEndBeforeAndBooker(any(), any(), (Pageable) any())).thenReturn(List.of(bookingTwo));
        List<BookingDto> bookings = bookingService.getBookingsByState(State.PAST, 1L, 0, 2);
        assertThat(bookings.size()).isEqualTo(1);
        assertThat(bookings).contains(toBookingDto);
        assertThat(bookings.get(0)).isEqualTo(toBookingDto);

    }

    @Test
    void getBookingsByStatePageableFUTURE() throws Exception {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userOne));
        when(bookingRepository.findAllByStartAfterAndBooker(any(), any(), (Pageable) any())).thenReturn(List.of(bookingOne));
        List<BookingDto> bookings = bookingService.getBookingsByState(State.FUTURE, 1L, 0, 2);
        assertThat(bookings.size()).isEqualTo(1);
        assertThat(bookings).contains(firstBookingDto);
        assertThat(bookings.get(0)).isEqualTo(firstBookingDto);
    }

    @Test
    void getBookingsByStatePageableRejectedOrWaiting() throws Exception {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userOne));
        when(bookingRepository.findAllByStatusAndBooker(any(), any(), (Pageable) any())).thenReturn(List.of(bookingOne));
        List<BookingDto> bookings = bookingService.getBookingsByState(State.REJECTED, 1L, 0, 2);
        assertThat(bookings.size()).isEqualTo(1);
        assertThat(bookings).contains(firstBookingDto);
        assertThat(bookings.get(0)).isEqualTo(firstBookingDto);
    }

    @Test
    void getBookingsByOwnerOfItemsAll() throws Exception {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userOne));
        when(bookingRepository.findAllByItemOwner(any(), (Sort) any())).thenReturn(List.of(bookingOne, bookingTwo));
        List<BookingDto> bookings = bookingService.getBookingsByOwnerOfItems(State.ALL, 1L);
        assertThat(bookings.size()).isEqualTo(2);
        assertThat(bookings).contains(firstBookingDto);
        assertThat(bookings).contains(toBookingDto);
        assertThat(bookings.get(0)).isEqualTo(firstBookingDto);
    }

    @Test
    void getBookingsByOwnerOfItemsExpectNotFoundExceptionBooking() {
        Assertions.assertThrows(NotFoundException.class, () -> {
            bookingService.getBookingsByOwnerOfItems(State.ALL, 1L);
        }, String.format("Аренда не найдена по айди %s", 1L));
    }

    @Test
    void getBookingsByOwnerOfItemsCURRENT() throws Exception {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userOne));
        when(bookingRepository.findAllByItemOwnerAndStartBeforeAndEndAfter(any(), any(), any(), (Sort) any())).thenReturn(List.of(bookingOne, bookingTwo));
        List<BookingDto> bookings = bookingService.getBookingsByOwnerOfItems(State.CURRENT, 1L);
        assertThat(bookings.size()).isEqualTo(2);
        assertThat(bookings).contains(firstBookingDto);
        assertThat(bookings).contains(toBookingDto);
        assertThat(bookings.get(0)).isEqualTo(firstBookingDto);
    }

    @Test
    void getBookingsByOwnerOfItemsPAST() throws Exception {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userOne));
        when(bookingRepository.findAllByItemOwnerAndEndBefore(any(), any(), (Sort) any())).thenReturn(List.of(bookingOne, bookingTwo));
        List<BookingDto> bookings = bookingService.getBookingsByOwnerOfItems(State.PAST, 1L);
        assertThat(bookings.size()).isEqualTo(2);
        assertThat(bookings).contains(firstBookingDto);
        assertThat(bookings).contains(toBookingDto);
        assertThat(bookings.get(0)).isEqualTo(firstBookingDto);
    }

    @Test
    void getBookingsByOwnerOfItemsFUTURE() throws Exception {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userOne));
        when(bookingRepository.findAllByItemOwnerAndStartAfter(any(), any(), (Sort) any())).thenReturn(List.of(bookingOne, bookingTwo));
        List<BookingDto> bookings = bookingService.getBookingsByOwnerOfItems(State.FUTURE, 1L);
        assertThat(bookings.size()).isEqualTo(2);
        assertThat(bookings).contains(firstBookingDto);
        assertThat(bookings).contains(toBookingDto);
        assertThat(bookings.get(0)).isEqualTo(firstBookingDto);
    }

    @Test
    void getBookingsByOwnerOfItemsRejectedOrWaiting() throws Exception {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userOne));
        when(bookingRepository.findAllByStatusAndItemOwner(any(), any(), (Sort) any())).thenReturn(List.of(bookingOne, bookingTwo));
        List<BookingDto> bookings = bookingService.getBookingsByOwnerOfItems(State.REJECTED, 1L);
        assertThat(bookings.size()).isEqualTo(2);
        assertThat(bookings).contains(firstBookingDto);
        assertThat(bookings).contains(toBookingDto);
        assertThat(bookings.get(0)).isEqualTo(firstBookingDto);
    }

    @Test
    void getBookingsByOwnerOfItemsPageableAll() throws Exception {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userOne));
        when(bookingRepository.findAllByItemOwner(any(), (Pageable) any())).thenReturn(List.of(bookingOne, bookingTwo));
        List<BookingDto> bookings = bookingService.getBookingsByOwnerOfItems(State.ALL, 1L, 0, 2);
        assertThat(bookings.size()).isEqualTo(2);
        assertThat(bookings).contains(firstBookingDto);
        assertThat(bookings).contains(toBookingDto);
        assertThat(bookings.get(0)).isEqualTo(firstBookingDto);
    }

    @Test
    void getBookingsByOwnerOfItemsPageableExpectNotFoundExceptionBooking() {
        Assertions.assertThrows(NotFoundException.class, () -> {
            bookingService.getBookingsByOwnerOfItems(State.ALL, 1L, 0, 2);
        }, String.format("Аренда не найдена по айди %s", 1L));
    }

    @Test
    void getBookingsByOwnerOfItemsPageableCURRENT() throws Exception {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userOne));
        when(bookingRepository.findAllByItemOwnerAndStartBeforeAndEndAfter(any(), any(), any(), (Pageable) any())).thenReturn(List.of(bookingOne, bookingTwo));
        List<BookingDto> bookings = bookingService.getBookingsByOwnerOfItems(State.CURRENT, 1L, 0, 2);
        assertThat(bookings.size()).isEqualTo(2);
        assertThat(bookings).contains(firstBookingDto);
        assertThat(bookings).contains(toBookingDto);
        assertThat(bookings.get(0)).isEqualTo(firstBookingDto);
    }

    @Test
    void getBookingsByOwnerOfItemsPageablePAST() throws Exception {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userOne));
        when(bookingRepository.findAllByItemOwnerAndEndBefore(any(), any(), (Pageable) any())).thenReturn(List.of(bookingOne, bookingTwo));
        List<BookingDto> bookings = bookingService.getBookingsByOwnerOfItems(State.PAST, 1L, 0, 2);
        assertThat(bookings.size()).isEqualTo(2);
        assertThat(bookings).contains(firstBookingDto);
        assertThat(bookings).contains(toBookingDto);
        assertThat(bookings.get(0)).isEqualTo(firstBookingDto);
    }

    @Test
    void getBookingsByOwnerOfItemsPageableFUTURE() throws Exception {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userOne));
        when(bookingRepository.findAllByItemOwnerAndStartAfter(any(), any(), (Pageable) any())).thenReturn(List.of(bookingOne, bookingTwo));
        List<BookingDto> bookings = bookingService.getBookingsByOwnerOfItems(State.FUTURE, 1L, 0, 2);
        assertThat(bookings.size()).isEqualTo(2);
        assertThat(bookings).contains(firstBookingDto);
        assertThat(bookings).contains(toBookingDto);
        assertThat(bookings.get(0)).isEqualTo(firstBookingDto);
    }

    @Test
    void getBookingsByOwnerOfItemsPageableRejectedOrWaiting() throws Exception {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userOne));
        when(bookingRepository.findAllByStatusAndItemOwner(any(), any(), (Pageable) any())).thenReturn(List.of(bookingOne, bookingTwo));
        List<BookingDto> bookings = bookingService.getBookingsByOwnerOfItems(State.REJECTED, 1L, 0, 2);
        assertThat(bookings.size()).isEqualTo(2);
        assertThat(bookings).contains(firstBookingDto);
        assertThat(bookings).contains(toBookingDto);
        assertThat(bookings.get(0)).isEqualTo(firstBookingDto);
    }

}