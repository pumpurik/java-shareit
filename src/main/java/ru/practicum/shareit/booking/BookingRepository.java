package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.dto.Status;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findFirstByBookerIdAndItemIdAndEndLessThanEqual(Long userId, Long itemId, LocalDateTime time);

    Optional<Booking> findFirstByItemIdAndStartLessThanEqualOrderByStartDesc(Long itemId, LocalDateTime time);

    Optional<Booking> findFirstByItemIdAndStartAfterOrderByStartAsc(Long itemId, LocalDateTime time);

    List<Booking> findAllByStatusAndBooker(Status state, User booker, Sort sort);

    List<Booking> findAllByStatusAndItemOwner(Status state, User itemOwner, Sort sort);

    List<Booking> findAllByItemOwner(User itemOwner, Sort sort);

    List<Booking> findAllByStartBeforeAndEndAfter(LocalDateTime start, LocalDateTime end, Sort sort);

    List<Booking> findAllByEndBefore(LocalDateTime end, Sort sort);

    List<Booking> findAllByStartAfter(LocalDateTime start, Sort sort);

    List<Booking> findAllByItemOwnerAndStartBeforeAndEndAfter(User itemOwner, LocalDateTime start, LocalDateTime end, Sort sort);

    List<Booking> findAllByItemOwnerAndEndBefore(User itemOwner, LocalDateTime end, Sort sort);

    List<Booking> findAllByItemOwnerAndStartAfter(User itemOwner, LocalDateTime start, Sort sort);
}
