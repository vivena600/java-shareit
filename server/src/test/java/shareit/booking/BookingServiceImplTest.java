package shareit.booking;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapperImpl;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {
    private final LocalDateTime testTime = LocalDateTime.of(2025, 07, 25, 00, 00, 00);

    @InjectMocks
    private BookingServiceImpl service;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Spy
    private final ItemMapper itemMapper = Mappers.getMapper(ItemMapper.class);  // Добавлено

    @Spy
    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);  // Добавлено

    @Spy
    private final BookingMapper bookingMapper = Mappers.getMapper(BookingMapper.class);

    private final User user1 = User.builder()
            .id(1L)
            .name("user1")
            .email("email1@ya.ru")
            .build();

    private final UserDto userDto = UserDto.builder()
            .id(1L)
            .name("user1")
            .email("email1@ya.ru")
            .build();

    private final Item item1 = Item.builder()
            .id(1L)
            .owner(user1)
            .name("item1")
            .description("desc1")
            .available(true)
            .build();

    private final ItemDto itemDto = ItemDto.builder()
            .id(1L)
            .owner(user1.getId())
            .name("item1")
            .description("desc1")
            .available(true)
            .build();

    private final Booking booking1 = Booking.builder()
            .id(1L)
            .item(item1)
            .booker(user1)
            .start(testTime.minusDays(1))
            .end(testTime.plusDays(1))
            .status(BookingStatus.APPROVED)
            .build();

    private final BookingDto bookingDto = BookingDto.builder()
            .id(1L)
            .booker(user1.getId())
            .itemId(item1.getId())
            .start(testTime.minusDays(1))
            .end(testTime.plusDays(1))
            .status(BookingStatus.APPROVED.toString())
            .build();

    private final BookingResponseDto responseDto = BookingResponseDto.builder()
            .id(1L)
            .item(itemDto)
            .booker(userDto)
            .start(testTime.minusDays(1))
            .end(testTime.plusDays(1))
            .status(BookingStatus.APPROVED.toString())
            .build();

    private final Booking bookingPast = Booking.builder()
            .start(testTime.minusYears(10))
            .end(testTime.minusYears(9))
            .item(item1)
            .booker(user1)
            .status(BookingStatus.APPROVED)
            .build();

    private final Booking bookingCurrent = Booking.builder()
            .start(testTime.minusYears(5))
            .end(testTime.plusYears(5))
            .item(item1)
            .booker(user1)
            .status(BookingStatus.APPROVED)
            .build();

    private final Booking bookingFuture = Booking.builder()
            .start(testTime.plusYears(8))
            .end(testTime.plusYears(9))
            .item(item1)
            .booker(user1)
            .status(BookingStatus.APPROVED)
            .build();

    private final Booking bookingRejected = Booking.builder()
            .start(testTime.plusYears(9))
            .end(testTime.plusYears(10))
            .item(item1)
            .booker(user1)
            .status(BookingStatus.REJECTED)
            .build();


    @Nested
    class Create {
        @Test
        public void createBooking() {
            when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));
            when(itemRepository.findById(item1.getId())).thenReturn(Optional.of(item1));
            when(bookingRepository.save(any())).thenReturn(booking1);

            when(bookingMapper.mapBookingResponseDto(any())).thenReturn(responseDto);

            BookingDto dto = bookingMapper.mapBookingDto(booking1);
            BookingResponseDto result = service.createBooking(user1.getId(), dto);

            verify(bookingRepository, times(1)).save(any());
            assertEquals(dto.getId(), result.getId());
            assertEquals(dto.getBooker(), result.getBooker().getId());
            assertEquals(dto.getStart(), result.getStart());
            assertEquals(dto.getEnd(), result.getEnd());
        }

        @Test
        public void createBookingWithFailUser() {
            Long userId = 99L;
            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            BookingDto dto = bookingMapper.mapBookingDto(booking1);

            NotFoundException ex = assertThrows(NotFoundException.class,
                    () -> service.createBooking(userId, dto));

            assertEquals("Не удалось найти пользователя с id:99", ex.getMessage());
            verify(bookingRepository, never()).save(any());
        }
    }

    @Nested
    class GetBookingsAllItemsByState {
        private final BookingResponseDto responsePast = BookingResponseDto.builder()
                .item(itemDto)
                .booker(userDto)
                .start(testTime.minusYears(10))
                .end(testTime.minusYears(9))
                .status(BookingStatus.APPROVED.toString())
                .build();

        private final BookingResponseDto responseCurrent = BookingResponseDto.builder()
                .item(itemDto)
                .booker(userDto)
                .start(testTime.minusYears(5))
                .end(testTime.plusYears(5))
                .status(BookingStatus.APPROVED.toString())
                .build();

        private final BookingResponseDto responseFuture = BookingResponseDto.builder()
                .item(itemDto)
                .booker(userDto)
                .start(testTime.plusYears(8))
                .end(testTime.plusYears(9))
                .status(BookingStatus.APPROVED.toString())
                .build();

        private final BookingResponseDto responseRejected = BookingResponseDto.builder()
                .item(itemDto)
                .booker(userDto)
                .start(testTime.plusYears(9))
                .end(testTime.plusYears(10))
                .status(BookingStatus.REJECTED.toString())
                .build();

        @Test
        void getAllBookingsForOwnerItems() {
            when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));
            when(bookingRepository.getBookingAllItemsByStateALL(user1.getId()))
                    .thenReturn(List.of(bookingPast, bookingCurrent, bookingFuture, bookingRejected));
            when(bookingMapper.mapListBookingResponseDto(anyList()))
                    .thenReturn(List.of(responsePast, responseCurrent, responseFuture, responseRejected));

            List<BookingResponseDto> result = service.getBookingsAllItemsByState("ALL", user1.getId());

            assertEquals(4, result.size());
            verify(bookingRepository).getBookingAllItemsByStateALL(user1.getId());
        }

        @Test
        void getRejectedBookingsForOwnerItems() {
            when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));
            when(bookingRepository.getBookingAllItemsByStateStatus(user1.getId(), BookingStatus.REJECTED))
                    .thenReturn(List.of(bookingRejected));
            when(bookingMapper.mapListBookingResponseDto(anyList()))
                    .thenReturn(List.of(responseRejected));

            List<BookingResponseDto> result = service.getBookingsAllItemsByState("REJECTED", user1.getId());

            assertEquals(1, result.size());
            assertEquals(BookingStatus.REJECTED.toString(), result.get(0).getStatus());
            verify(bookingRepository).getBookingAllItemsByStateStatus(user1.getId(), BookingStatus.REJECTED);
        }

        @Test
        void getCurrentBookingsForOwnerItems() {
            when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));
            when(bookingRepository.getBookingAllItemsByStateCurrent(eq(user1.getId()), any(LocalDateTime.class)))
                    .thenReturn(List.of(bookingCurrent));
            when(bookingMapper.mapListBookingResponseDto(anyList()))
                    .thenReturn(List.of(responseCurrent));

            List<BookingResponseDto> result = service.getBookingsAllItemsByState("CURRENT", user1.getId());

            assertEquals(1, result.size());
            verify(bookingRepository).getBookingAllItemsByStateCurrent(eq(user1.getId()), any(LocalDateTime.class));
        }

        @Test
        void getFutureBookingsForOwnerItems() {
            when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));
            when(bookingRepository.getBookingAllItemsByStateFuture(eq(user1.getId()), any(LocalDateTime.class)))
                    .thenReturn(List.of(bookingCurrent));
            when(bookingMapper.mapListBookingResponseDto(anyList()))
                    .thenReturn(List.of(responseFuture));

            List<BookingResponseDto> result = service.getBookingsAllItemsByState("FUTURE", user1.getId());

            assertEquals(1, result.size());
            verify(bookingRepository).getBookingAllItemsByStateFuture(eq(user1.getId()), any(LocalDateTime.class));
        }

        @Test
        void getBookingsForNonExistingUser() {
            Long userId = 99L;
            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class,
                    () -> service.getBookingsAllItemsByState("ALL", userId));
        }
    }
}
