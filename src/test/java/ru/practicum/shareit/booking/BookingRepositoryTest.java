package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.booking.dto.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class BookingRepositoryTest {
    @Autowired
    private TestEntityManager em;
    @Autowired
    private BookingRepository bookRepository;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private final static String startTimeOne = "2023-06-21T01:29:22";
    private final static String endTimeOne = "2023-09-22T19:14:48";
    private final static String startTimeTwo = "2023-10-21T01:29:22";
    private final static String endTimeTwo = "2023-06-30T02:30:22";
    private final static String nowDate = "2023-10-20T18:14:49";
    private final static String nowDateTwo = "2023-07-24T01:29:22";


    @Test
    void findFirstByBookerIdAndItemIdAndEndLessThanEqual() {
        Item item = em.find(Item.class, 3L);
        User user = em.find(User.class, 1L);
        Optional<Booking> findBooking = bookRepository
                .findFirstByBookerIdAndItemIdAndEndLessThanEqual(1L, 3L, LocalDateTime.parse(nowDate, formatter));
        assertThat(findBooking).isPresent();
        assertThat(findBooking.get().getId()).isEqualTo(1L);
        assertThat(findBooking.get().getItem()).isEqualTo(item);
        assertThat(findBooking.get().getBooker()).isEqualTo(user);
    }

    @Test
    void findFirstByItemIdAndStartLessThanEqualOrderByStartDesc() {
        Item item = em.find(Item.class, 1L);
        User user = em.find(User.class, 3L);
        Optional<Booking> findBooking = bookRepository
                .findFirstByItemIdAndStartLessThanEqualOrderByStartDesc(1L, LocalDateTime.parse(nowDate, formatter));
        assertThat(findBooking).isPresent();
        assertThat(findBooking.get().getId()).isEqualTo(8L);
        assertThat(findBooking.get().getItem()).isEqualTo(item);
        assertThat(findBooking.get().getBooker()).isEqualTo(user);

    }

    @Test
    void findFirstByItemIdAndStartAfterOrderByStartAsc() {
        Item item = em.find(Item.class, 1L);
        User user = em.find(User.class, 2L);
        Optional<Booking> findBooking = bookRepository
                .findFirstByItemIdAndStartAfterOrderByStartAsc(1L, LocalDateTime.parse(nowDateTwo, formatter));
        assertThat(findBooking).isPresent();
        assertThat(findBooking.get().getId()).isEqualTo(3L);
        assertThat(findBooking.get().getItem()).isEqualTo(item);
        assertThat(findBooking.get().getBooker()).isEqualTo(user);
    }

    @Test
    void findAllByStatusAndBookerPageable() {
        Booking bookingTwo = em.find(Booking.class, 2L);
        User user = em.find(User.class, 1L);
        List<Booking> findBooking = bookRepository
                .findAllByStatusAndBooker(Status.APPROVED, user, PageRequest.of(0, 2, Sort.Direction.DESC, "start"));
        assertThat(findBooking.size()).isEqualTo(2);
        assertThat(findBooking.get(0)).isEqualTo(bookingTwo);
    }

    @Test
    void findAllByStatusAndBookerSort() {
        User user = em.find(User.class, 1L);
        List<Booking> findBooking = bookRepository
                .findAllByStatusAndBooker(Status.APPROVED, user, Sort.by(Sort.Direction.DESC, "start"));
        assertThat(findBooking.size()).isEqualTo(4);
    }

    @Test
    void findAllByStatusAndItemOwnerSort() {
        User user = em.find(User.class, 1L);
        Booking booking = em.find(Booking.class, 3L);
        List<Booking> findBooking = bookRepository
                .findAllByStatusAndItemOwner(Status.REJECTED, user, Sort.by(Sort.Direction.DESC, "start"));
        assertThat(findBooking.size()).isEqualTo(1);
        assertThat(findBooking).contains(booking);
    }

    @Test
    void findAllByItemOwnerSort() {
        User user = em.find(User.class, 1L);
        Booking booking = em.find(Booking.class, 3L);
        Booking bookingOther = em.find(Booking.class, 8L);
        List<Booking> findBooking = bookRepository
                .findAllByItemOwner(user, Sort.by(Sort.Direction.DESC, "start"));
        assertThat(findBooking.size()).isEqualTo(2);
        assertThat(findBooking).contains(booking, bookingOther);
        assertThat(findBooking.get(0)).isEqualTo(bookingOther);
    }

    @Test
    void findAllByStatusAndItemOwnerPageable() {
        User user = em.find(User.class, 1L);
        Booking booking = em.find(Booking.class, 3L);
        List<Booking> findBooking = bookRepository
                .findAllByStatusAndItemOwner(Status.REJECTED, user, PageRequest.of(0, 2, Sort.Direction.DESC, "start"));
        assertThat(findBooking.size()).isEqualTo(1);
        assertThat(findBooking).contains(booking);
    }

    @Test
    void testFindAllByItemOwnerPageable() {
        User user = em.find(User.class, 1L);
        Booking booking = em.find(Booking.class, 3L);
        Booking bookingOther = em.find(Booking.class, 8L);
        List<Booking> findBooking = bookRepository
                .findAllByItemOwner(user, PageRequest.of(0, 3, Sort.Direction.DESC, "start"));
        assertThat(findBooking.size()).isEqualTo(2);
        assertThat(findBooking).contains(booking, bookingOther);
        assertThat(findBooking.get(0)).isEqualTo(bookingOther);
    }

    @Test
    void findAllByStartBeforeAndEndAfterSort() {
        Booking bookingTwo = em.find(Booking.class, 2L);
        Booking booking = em.find(Booking.class, 3L);
        Booking bookingOther = em.find(Booking.class, 8L);
        List<Booking> findBooking = bookRepository
                .findAllByStartBeforeAndEndAfter(LocalDateTime.parse(startTimeTwo, formatter),
                        LocalDateTime.parse(endTimeTwo, formatter), Sort.by(Sort.Direction.DESC, "start"));
        assertThat(findBooking.size()).isEqualTo(8);
        assertThat(findBooking).contains(bookingTwo, booking, bookingOther);
        assertThat(findBooking.get(0)).isEqualTo(bookingOther);
    }

    @Test
    void findAllByEndBeforeSort() {
        Booking booking = em.find(Booking.class, 3L);
        List<Booking> findBooking = bookRepository
                .findAllByEndBefore(LocalDateTime.parse(endTimeOne, formatter), Sort.by(Sort.Direction.DESC, "start"));
        assertThat(findBooking.size()).isEqualTo(7);
        assertThat(findBooking.get(0)).isEqualTo(booking);
    }

    @Test
    void findAllByStartAfterSort() {
        Booking booking = em.find(Booking.class, 8L);
        List<Booking> findBooking = bookRepository
                .findAllByStartAfter(LocalDateTime.parse(startTimeOne, formatter), Sort.by(Sort.Direction.DESC, "start"));
        assertThat(findBooking.size()).isEqualTo(8);
        assertThat(findBooking.get(0)).isEqualTo(booking);
    }

    @Test
    void findAllByStartBeforeAndEndAfterPageable() {
        Booking bookingTwo = em.find(Booking.class, 2L);
        User user = em.find(User.class, 1L);
        List<Booking> findBooking = bookRepository
                .findAllByStartBeforeAndEndAfterAndBooker(LocalDateTime.parse(startTimeTwo, formatter),
                        LocalDateTime.parse(endTimeTwo, formatter), user, PageRequest.of(0, 3, Sort.Direction.DESC, "start"));
        assertThat(findBooking.size()).isEqualTo(3);
        assertThat(findBooking.get(0)).isEqualTo(bookingTwo);
    }

    @Test
    void findAllByEndBeforePageable() {
        Booking booking = em.find(Booking.class, 2L);
        User user = em.find(User.class, 1L);
        List<Booking> findBooking = bookRepository
                .findAllByEndBeforeAndBooker(LocalDateTime.parse(endTimeOne, formatter), user, PageRequest.of(0, 3, Sort.Direction.DESC, "start"));
        assertThat(findBooking.size()).isEqualTo(3);
        assertThat(findBooking.get(0)).isEqualTo(booking);
    }

    @Test
    void findAllByStartAfterPageable() {
        Booking booking = em.find(Booking.class, 2L);
        User user = em.find(User.class, 1L);
        List<Booking> findBooking = bookRepository
                .findAllByStartAfterAndBooker(LocalDateTime.parse(startTimeOne, formatter), user, PageRequest.of(0, 3, Sort.Direction.DESC, "start"));
        assertThat(findBooking.size()).isEqualTo(3);
        assertThat(findBooking.get(0)).isEqualTo(booking);
    }

    @Test
    void findAllByItemOwnerAndStartBeforeAndEndAfterSort() {
        User user = em.find(User.class, 1L);
        Booking bookingTwo = em.find(Booking.class, 3L);
        Booking bookingOther = em.find(Booking.class, 8L);
        List<Booking> findBooking = bookRepository
                .findAllByItemOwnerAndStartBeforeAndEndAfter(user, LocalDateTime.parse(startTimeTwo, formatter),
                        LocalDateTime.parse(endTimeTwo, formatter), Sort.by(Sort.Direction.DESC, "start"));
        assertThat(findBooking.size()).isEqualTo(2);
        assertThat(findBooking).contains(bookingTwo, bookingOther);
        assertThat(findBooking.get(0)).isEqualTo(bookingOther);
    }

    @Test
    void findAllByItemOwnerAndEndBeforeSort() {
        User user = em.find(User.class, 1L);
        Booking bookingTwo = em.find(Booking.class, 3L);
        Booking bookingOther = em.find(Booking.class, 8L);
        List<Booking> findBooking = bookRepository
                .findAllByItemOwnerAndEndBefore(user, LocalDateTime.parse(startTimeTwo, formatter), Sort.by(Sort.Direction.DESC, "start"));
        assertThat(findBooking.size()).isEqualTo(2);
        assertThat(findBooking).contains(bookingTwo, bookingOther);
        assertThat(findBooking.get(0)).isEqualTo(bookingOther);
    }

    @Test
    void findAllByItemOwnerAndStartAfterSort() {
        User user = em.find(User.class, 1L);
        Booking bookingTwo = em.find(Booking.class, 3L);
        Booking bookingOther = em.find(Booking.class, 8L);
        List<Booking> findBooking = bookRepository
                .findAllByItemOwnerAndStartAfter(user, LocalDateTime.parse(startTimeOne, formatter), Sort.by(Sort.Direction.DESC, "start"));
        assertThat(findBooking.size()).isEqualTo(2);
        assertThat(findBooking).contains(bookingTwo, bookingOther);
        assertThat(findBooking.get(0)).isEqualTo(bookingOther);
    }

    @Test
    void findAllByItemOwnerAndStartBeforeAndEndAfterPageable() {
        User user = em.find(User.class, 1L);
        Booking bookingTwo = em.find(Booking.class, 3L);
        Booking bookingOther = em.find(Booking.class, 8L);
        List<Booking> findBooking = bookRepository
                .findAllByItemOwnerAndStartBeforeAndEndAfter(user, LocalDateTime.parse(startTimeTwo, formatter),
                        LocalDateTime.parse(endTimeTwo, formatter), PageRequest.of(0, 3, Sort.Direction.DESC, "start"));
        assertThat(findBooking.size()).isEqualTo(2);
        assertThat(findBooking).contains(bookingTwo, bookingOther);
        assertThat(findBooking.get(0)).isEqualTo(bookingOther);

    }

    @Test
    void findAllByItemOwnerAndEndBeforePageable() {
        User user = em.find(User.class, 1L);
        Booking bookingOther = em.find(Booking.class, 8L);
        List<Booking> findBooking = bookRepository
                .findAllByItemOwnerAndEndBefore(user, LocalDateTime.parse(startTimeTwo, formatter), PageRequest.of(0, 1, Sort.Direction.DESC, "start"));
        assertThat(findBooking.size()).isEqualTo(1);
        assertThat(findBooking).contains(bookingOther);
        assertThat(findBooking.get(0)).isEqualTo(bookingOther);
    }

    @Test
    void findAllByItemOwnerAndStartAfterPageable() {
        User user = em.find(User.class, 1L);
        Booking bookingOther = em.find(Booking.class, 8L);
        List<Booking> findBooking = bookRepository
                .findAllByItemOwnerAndStartAfter(user, LocalDateTime.parse(startTimeOne, formatter), PageRequest.of(0, 1, Sort.Direction.DESC, "start"));
        assertThat(findBooking.size()).isEqualTo(1);
        assertThat(findBooking).contains(bookingOther);
        assertThat(findBooking.get(0)).isEqualTo(bookingOther);
    }
}