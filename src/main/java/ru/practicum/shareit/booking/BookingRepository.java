package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.dto.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findFirstByBookerIdAndItemIdAndEndLessThanEqual(Long bookerId, Long itemId, LocalDateTime end);

    Optional<Booking> findFirstByItemIdAndStartLessThanEqualOrderByStartDesc(Long itemId, LocalDateTime time);

    Optional<Booking> findFirstByItemIdAndStartAfterOrderByStartAsc(Long itemId, LocalDateTime time);

    //  List<Booking> findAllByBooker(User user);
    Page<Booking> findAllByBooker(User user, Pageable pageable);

    List<Booking> findAllByStatusAndBooker(Status state, User booker, Pageable pageable);

    List<Booking> findAllByStatusAndBooker(Status state, User booker, Sort sort);

    List<Booking> findAllByStatusAndItemOwner(Status state, User itemOwner, Sort sort);

    List<Booking> findAllByItemOwner(User itemOwner, Sort sort);

    List<Booking> findAllByStatusAndItemOwner(Status state, User itemOwner, Pageable pageable);

    List<Booking> findAllByItemOwner(User itemOwner, Pageable pageable);

    List<Booking> findAllByStartBeforeAndEndAfterAndBooker(LocalDateTime start, LocalDateTime end, User booker, Pageable pageable);

    List<Booking> findAllByEndBeforeAndBooker(LocalDateTime end, User booker, Pageable pageable);

    List<Booking> findAllByStartAfterAndBooker(LocalDateTime start, User booker, Pageable pageable);

    List<Booking> findAllByStartBeforeAndEndAfter(LocalDateTime start, LocalDateTime end, Sort sort);

    List<Booking> findAllByEndBefore(LocalDateTime end, Sort sort);

    List<Booking> findAllByStartAfter(LocalDateTime start, Sort sort);

    List<Booking> findAllByItemOwnerAndStartBeforeAndEndAfter(User itemOwner, LocalDateTime start, LocalDateTime end, Sort sort);

    List<Booking> findAllByItemOwnerAndEndBefore(User itemOwner, LocalDateTime end, Sort sort);

    List<Booking> findAllByItemOwnerAndStartAfter(User itemOwner, LocalDateTime start, Sort sort);

    List<Booking> findAllByItemOwnerAndStartBeforeAndEndAfter(User itemOwner, LocalDateTime start, LocalDateTime end, Pageable pageable);

    List<Booking> findAllByItemOwnerAndEndBefore(User itemOwner, LocalDateTime end, Pageable pageable);

    List<Booking> findAllByItemOwnerAndStartAfter(User itemOwner, LocalDateTime start, Pageable pageable);
}
