package ru.practicum.shareit.booking;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;

@UtilityClass
public class BookingMapper {

    public Booking mapBooking(BookingDto bookingDto, User user, Item item) {
        return Booking.builder()
                .id(bookingDto.getId())
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .item(item)
                .booker(user)
                .status(BookingStatus.valueOf(bookingDto.getStatus()))
                .build();
    }

    public BookingDto mapBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(String.valueOf(booking.getStatus()))
                .booker(booking.getBooker().getId())
                .itemId(booking.getItem().getId())
                .build();
    }

    public BookingResponseDto mapBookingResponseDto(Booking booking) {
        return BookingResponseDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(String.valueOf(booking.getStatus()))
                .booker(mapUserToDto(booking.getBooker()))
                .item(mapItemToDto(booking.getItem()))
                .build();
    }

    private UserDto mapUserToDto(User user) {
        if (user == null) return null;

        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    private ItemDto mapItemToDto(Item item) {
        if (item == null) return null;

        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .owner(item.getOwner().getId())
                .build();
    }
}
