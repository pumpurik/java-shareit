package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.booking.dto.BookingTwoFieldsDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;

import java.util.ArrayList;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.isAvailable(),
                item.getOwner(),
                item.getRequest() != null ? item.getRequest().getId() : null
        );
    }

    public static ItemDtoWithBooking toItemDtoWithBooking(Item item) {
        return new ItemDtoWithBooking(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.isAvailable(),
                new BookingTwoFieldsDto(),
                new BookingTwoFieldsDto(),
                new ArrayList<>()
        );
    }

    public static ItemDtoWithBooking toItemDtoWithBookingDtoForUser(Item item) {
        return new ItemDtoWithBooking(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.isAvailable(),
                new ArrayList<>()
        );
    }

    public static Item toItem(ItemDto itemDto, ItemRequest itemRequest) {
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable() == null ? Boolean.TRUE : itemDto.getAvailable(),
                itemDto.getOwner(),
                itemDto.getRequestId() != null ? itemRequest : null
        );
    }
//    public static Item toItem(ItemDto itemDto) {
//        return new Item(
//                itemDto.getId(),
//                itemDto.getName(),
//                itemDto.getDescription(),
//                itemDto.getAvailable() == null ? Boolean.TRUE : itemDto.getAvailable(),
//                itemDto.getOwner(),
//                itemDto.get
//        );
//    }

    public static Item toItemWithBlankFields(ItemDto itemDto, Item item) {
        item.setName(itemDto.getName() != null ? itemDto.getName() : item.getName());
        item.setDescription(itemDto.getDescription() != null ? itemDto.getDescription() : item.getDescription());
        item.setAvailable(itemDto.getAvailable() != null ? itemDto.getAvailable() : (item.isAvailable()));
        return item;
    }

}
