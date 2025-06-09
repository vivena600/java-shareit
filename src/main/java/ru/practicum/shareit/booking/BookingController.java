package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    /**
     * Создание брони.
     * Post /bookings
     * Headers X-Sharer-User-Id
     */
    @PostMapping
    public BookingResponseDto createBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @RequestBody @Valid BookingDto bookingDto) {
        return bookingService.createBooking(userId, bookingDto);
    }

    /**
     * Изменение состояния брони.
     * Patch /bookings/bookingId?approved
     * Headers X-Sharer-User-Id
     */
    @PatchMapping("/{bookingId}")
    public BookingResponseDto approveBooking(@PathVariable("bookingId") Long bookingId,
                                     @RequestParam("approved") Boolean approved,
                                     @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.approveBooking(userId, bookingId, approved);
    }

    /**
     * Получение информации о бронировании.
     * Post /bookings/bookingId
     * Headers X-Sharer-User-Id
     */
    @GetMapping("/{bookingId}")
    public List<BookingDto> getBookings(@PathVariable("bookingId") Long bookingId,
                                        @RequestHeader("X-Sharer-User-Id") Long userId) {
        return null;
    }
}
