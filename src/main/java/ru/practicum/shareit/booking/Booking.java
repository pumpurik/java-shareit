package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.dto.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

/**
 * TODO Sprint add-bookings.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Booking {
    long id;
    String start;
    String end;
    Item item;
    User booker;
    Status status;
}
