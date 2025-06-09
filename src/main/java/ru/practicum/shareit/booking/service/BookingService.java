package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

public interface BookingService {

    public BookingResponseDto createBooking(Long userId, BookingDto bookingDto);

    BookingResponseDto approveBooking(Long userId, Long bookingId, Boolean approved);

    BookingDto getBooking(Long bookingId);
}
