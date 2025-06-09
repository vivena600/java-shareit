package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

public interface BookingService {

    BookingResponseDto createBooking(Long userId, BookingDto bookingDto);

    BookingResponseDto approveBooking(Long userId, Long bookingId, Boolean approved);

    BookingResponseDto canceledBooking(Long userId, Long bookingId);

    BookingDto getBooking(Long bookingId);
}
