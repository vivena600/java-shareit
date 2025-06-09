package ru.practicum.shareit.booking.service;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository repository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public BookingResponseDto createBooking(Long userId, BookingDto bookingDto) {
        log.info("Пользователь с id {} создает бронь", userId);
        User user = getUser(userId);
        Item item = checkItem(bookingDto.getItemId());
        if (!item.getAvailable()) {
            throw new ValidationException("Вещь с id " + bookingDto.getItemId() + " недоступна для бронирования");
        }
        bookingDto.setStatus(String.valueOf(BookingStatus.WAITING));
        Booking bookingEntity = BookingMapper.mapBooking(bookingDto, user, item);
        return BookingMapper.mapBookingResponseDto(repository.save(bookingEntity));
    }

    @Override
    public BookingResponseDto approveBooking(Long userId, Long bookingId, Boolean approved) {
        Booking booking = checkBooking(bookingId);

        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ValidationException("Статус бронирования уже изменен");
        }

        Item item = checkItem(booking.getItem().getId());
        if (!item.getOwner().getId().equals(userId)) {
            throw new ValidationException("Пользователь с id " + userId + " не может редактировать статус этой вещи");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return BookingMapper.mapBookingResponseDto(repository.save(booking));
    }

    @Override
    public BookingDto getBooking(Long bookingId) {
        return null;
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Не удалось найти пользователя с id:" + userId));
    }

    private Item checkItem(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Не удалось найти вещь с id:" + itemId));
    }

    private Booking checkBooking(Long bookingId) {
        return repository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Не удалось найти бронь с id:" + bookingId));
    }
}
