package shareit.item;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.exception.UnavailableActionError;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithCommentDto;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {
    private final LocalDateTime testTime = LocalDateTime.of(2025, 07, 25, 00, 00, 00);

    @Mock
    private ItemRepository repository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private RequestRepository requestRepository;

    @Spy
    private final ItemMapper mapper = Mappers.getMapper(ItemMapper.class);

    @Spy
    private final CommentMapper commentMapper = Mappers.getMapper(CommentMapper.class);

    @Spy
    private final BookingMapper bookingMapper = Mappers.getMapper(BookingMapper.class);

    @InjectMocks
    private ItemServiceImpl service;

    private final User user1 = User.builder()
            .id(1L)
            .name("user1")
            .email("email1@ya.ru")
            .build();

    private final ItemRequest request = ItemRequest.builder()
            .id(1L)
            .requester(user1)
            .created(testTime)
            .description("desc1")
            .build();

    private final Item itemWithRequest = Item.builder()
            .id(3L)
            .owner(user1)
            .name("item3")
            .description("desc2")
            .available(true)
            .request(request)
            .build();

    private final Item item1 = Item.builder()
            .id(1L)
            .owner(user1)
            .name("item1")
            .description("desc1")
            .available(true)
            .build();

    private final Item item2 = Item.builder()
            .id(2L)
            .owner(user1)
            .name("item2")
            .description("desc2")
            .available(true)
            .build();

    private final Comment comment1 = Comment.builder()
            .id(1L)
            .item(item1)
            .text("text1")
            .createdAt(LocalDateTime.now())
            .author(user1)
            .build();

    private final Booking booking1 = Booking.builder()
            .id(1L)
            .item(item1)
            .booker(user1)
            .start(LocalDateTime.now().minusDays(1))
            .end(LocalDateTime.now())
            .build();

    private void checkItem(ItemDto dto1, ItemDto dto2) {
        assertEquals(dto1.getId(), dto2.getId());
        assertEquals(dto1.getName(), dto2.getName());
        assertEquals(dto1.getDescription(), dto2.getDescription());
        assertEquals(dto1.getAvailable(), dto2.getAvailable());
        assertEquals(dto1.getOwner(), dto2.getOwner());
    }

    @Nested
    class CreateTest {

        @Test
        public void createItem() {
            when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));
            when(repository.save(any())).thenReturn(item1);
            ItemDto dto = mapper.mapItemDto(item1);

            ItemDto result = service.createItem(user1.getId(), dto);

            verify(repository, times(1)).save(any());
            checkItem(dto, result);
        }

        @Test
        public void createItemWithFailUser() {
            Long userId = 99L;
            ItemDto dto = mapper.mapItemDto(item1);

            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            NotFoundException ex = assertThrows(NotFoundException.class,
                    () -> service.createItem(userId, dto));

            assertEquals("Не удалось найти пользователя с id 99", ex.getMessage());
            verify(repository, never()).save(any());
        }

        @Test
        public void createItemWithRequest() {
            when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));
            when(requestRepository.findById(itemWithRequest.getRequest().getId())).thenReturn(Optional.of(request));
            when(repository.save(any())).thenReturn(itemWithRequest);
            ItemDto dto = mapper.mapItemDto(itemWithRequest);

            ItemDto result = service.createItem(user1.getId(), dto);

            verify(repository, times(1)).save(any());
            checkItem(dto, result);
        }

        @Test
        public void createItemWithFailRequest() {
            Long requestId = 99L;
            when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));
            when(requestRepository.findById(requestId)).thenReturn(Optional.empty());

            itemWithRequest.getRequest().setId(requestId);
            ItemDto dto = mapper.mapItemDto(itemWithRequest);

            NotFoundException ex = assertThrows(NotFoundException.class,
                    () -> service.createItem(user1.getId(), dto));

            assertEquals("Не удалось найти запрос с id 99", ex.getMessage());
            verify(repository, never()).save(any());
        }
    }

    @Nested
    class UpdateItemTest {

        @Test
        void updateItem() {
            Long userId = user1.getId();
            Long itemId = item1.getId();
            when(userRepository.findById(userId)).thenReturn(Optional.of(user1));
            when(repository.findById(itemId)).thenReturn(Optional.of(item1));
            ItemDto updatedDto = ItemDto.builder()
                    .id(itemId)
                    .name("updatedName")
                    .description("updatedDescription")
                    .available(false)
                    .build();

            when(repository.save(any(Item.class))).thenAnswer(invocation ->
                    invocation.getArgument(0));
            ItemDto result = service.updateItem(userId, itemId, updatedDto);

            assertEquals(updatedDto.getName(), result.getName());
            assertEquals(updatedDto.getDescription(), result.getDescription());
            assertEquals(updatedDto.getAvailable(), result.getAvailable());

            verify(repository).findById(itemId);
            verify(repository).save(any(Item.class));
        }

        @Test
        void updateItemWithItemNotFound() {
            Long userId = user1.getId();
            Long itemId = 999L;

            when(userRepository.findById(userId)).thenReturn(Optional.of(user1));
            when(repository.findById(itemId)).thenReturn(Optional.empty());

            ItemDto dto = ItemDto.builder()
                    .id(itemId)
                    .name("updatedName")
                    .description("updatedDescription")
                    .available(false)
                    .build();

            NotFoundException ex = assertThrows(NotFoundException.class, () -> service.updateItem(userId, itemId, dto));
            assertEquals("Не удается найти вещь с id " + itemId, ex.getMessage());

            verify(repository).findById(itemId);
            verify(repository, never()).save(any());
        }

        @Test
        void updateItemNotOwner() {
            Long userId = user1.getId();
            Long itemId = item2.getId();

            User anotherUser = User.builder().id(99L).name("other").email("other@ya.ru").build();
            Item itemOwnedByOther = Item.builder()
                    .id(itemId)
                    .owner(anotherUser)
                    .name("itemName")
                    .description("desc")
                    .available(true)
                    .build();

            when(userRepository.findById(userId)).thenReturn(Optional.of(user1));
            when(repository.findById(itemId)).thenReturn(Optional.of(itemOwnedByOther));

            ItemDto dto = ItemDto.builder()
                    .owner(99L)
                    .build();

            UnavailableActionError ex = assertThrows(UnavailableActionError.class,
                    () -> service.updateItem(userId, itemId, dto));

            assertEquals("Пользователь с id = " + userId + "не может редактировать эту вещь", ex.getMessage());

            verify(repository).findById(itemId);
            verify(repository, never()).save(any());
        }
    }

    @Nested
    class GetTest {
        @Test
        void getItemWithCommentsAndBookings() {
            Booking lastBooking = Booking.builder()
                    .id(1L)
                    .start(testTime.minusDays(2))
                    .end(testTime.minusDays(1))
                    .booker(user1)
                    .item(item1)
                    .status(BookingStatus.APPROVED)
                    .build();

            Booking nextBooking = Booking.builder()
                    .id(2L)
                    .start(testTime.plusDays(1))
                    .end(testTime.plusDays(2))
                    .booker(user1)
                    .item(item1)
                    .status(BookingStatus.APPROVED)
                    .build();

            BookingDto lastBookingDto = BookingDto.builder()
                    .id(1L)
                    .start(testTime.minusDays(2))
                    .end(testTime.minusDays(1))
                    .booker(user1.getId())
                    .status(BookingStatus.APPROVED.toString())
                    .build();

            BookingDto nextBookingDto = BookingDto.builder()
                    .id(2L)
                    .start(testTime.plusDays(1))
                    .end(testTime.plusDays(2))
                    .booker(user1.getId())
                    .status(BookingStatus.APPROVED.toString())
                    .build();

            Comment comment = Comment.builder()
                    .id(1L)
                    .text("Great item!")
                    .author(user1)
                    .item(item1)
                    .createdAt(testTime)
                    .build();

            when(repository.findById(item1.getId())).thenReturn(Optional.of(item1));
            when(commentRepository.findCommentsByItemId(item1.getId())).thenReturn(List.of(comment));
            when(bookingMapper.mapBookingDto(nextBooking)).thenReturn(nextBookingDto);
            when(bookingMapper.mapBookingDto(lastBooking)).thenReturn(lastBookingDto);
            when(bookingRepository.findLastBooking(eq(item1.getId()), any(LocalDateTime.class))).thenReturn(Optional.of(lastBooking));
            when(bookingRepository.findNextBooking(eq(item1.getId()), any(LocalDateTime.class))).thenReturn(Optional.of(nextBooking));

            ItemWithCommentDto result = service.getItem(item1.getId());

            assertNotNull(result);
            assertEquals(1, result.getComments().size());
            assertNotNull(result.getLastBooking());
            assertNotNull(result.getNextBooking());
            assertEquals(1L, result.getLastBooking().getId());
            assertEquals(2L, result.getNextBooking().getId());
        }

        @Test
        public void getItemById() {
            when(repository.findById(item1.getId())).thenReturn(Optional.of(item1));
            when(commentRepository.findCommentsByItemId(item1.getId())).thenReturn(Collections.EMPTY_LIST);
            when(bookingRepository.findLastBooking(anyLong(), any())).thenReturn(Optional.empty());
            when(bookingRepository.findNextBooking(anyLong(), any())).thenReturn(Optional.empty());
            ItemDto dto = mapper.mapItemDto(item1);

            ItemWithCommentDto result = service.getItem(item1.getId());

            verify(repository, times(1)).findById(item1.getId());

            assertEquals(dto.getId(), result.getId());
            assertEquals(dto.getName(), result.getName());
            assertEquals(dto.getDescription(), result.getDescription());
            assertEquals(dto.getAvailable(), result.getAvailable());
            assertEquals(dto.getOwner(), result.getOwner());
        }

        @Test
        void getItemDataMapping() {
            when(repository.findById(item1.getId())).thenReturn(Optional.of(item1));
            when(commentRepository.findCommentsByItemId(item1.getId())).thenReturn(Collections.emptyList());
            when(bookingRepository.findLastBooking(anyLong(), any())).thenReturn(Optional.empty());
            when(bookingRepository.findNextBooking(anyLong(), any())).thenReturn(Optional.empty());

            ItemWithCommentDto result = service.getItem(item1.getId());

            assertEquals(item1.getId(), result.getId());
            assertEquals(item1.getName(), result.getName());
            assertEquals(item1.getDescription(), result.getDescription());
            assertEquals(item1.getAvailable(), result.getAvailable());
            assertEquals(item1.getOwner().getId(), result.getOwner());
        }

        @Test
        public void getItemByFailId() {
            Long failId = 99L;
            when(repository.findById(failId)).thenReturn(Optional.empty());

            NotFoundException ex = assertThrows(NotFoundException.class,
                    () -> service.getItem(failId));

            assertEquals("Не удается найти вещь с id 99", ex.getMessage());
            verify(repository, never()).save(any());
        }

        @Test
        public void getUserItems() {
            List<Item> items = List.of(item1, item2);
            List<Long> itemIds = items.stream().map(Item::getId).toList();

            CommentDto commentDto = commentMapper.mapCommentDto(comment1);

            when(repository.findByOwnerId(user1.getId())).thenReturn(items);
            when(repository.findByOwner(user1)).thenReturn(items);
            when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));
            when(commentRepository.findCommentByItemIdIn(itemIds)).thenReturn(List.of(comment1));
            when(bookingRepository.findLastBooking(eq(item1.getId()), any())).thenReturn(Optional.of(booking1));
            when(bookingRepository.findNextBooking(eq(item1.getId()), any())).thenReturn(Optional.empty());
            when(bookingRepository.findLastBooking(eq(item2.getId()), any())).thenReturn(Optional.empty());
            when(bookingRepository.findNextBooking(eq(item2.getId()), any())).thenReturn(Optional.empty());

            List<ItemWithCommentDto> result = service.getUserItems(user1.getId());

            assertEquals(2, result.size());

            ItemWithCommentDto result1 = result.stream()
                    .filter(dto -> dto.getId().equals(item1.getId()))
                    .findFirst()
                    .orElseThrow();

            assertEquals(item1.getName(), result1.getName());
            assertEquals(1, result1.getComments().size());
            assertEquals(comment1.getText(), result1.getComments().get(0).getText());

            assertNotNull(result1.getLastBooking());
            assertEquals(booking1.getId(), result1.getLastBooking().getId());

            assertNull(result1.getNextBooking());

            ItemWithCommentDto result2 = result.stream()
                    .filter(dto -> dto.getId().equals(item2.getId()))
                    .findFirst()
                    .orElseThrow();

            assertEquals(item2.getName(), result2.getName());
            assertTrue(result2.getComments().isEmpty());
            assertNull(result2.getLastBooking());
            assertNull(result2.getNextBooking());

            verify(repository, times(1)).findByOwnerId(user1.getId());
            verify(repository, times(1)).findByOwner(user1);
            verify(commentRepository, times(1)).findCommentByItemIdIn(itemIds);
        }

        @Test
        void getUserItemsUserNotFound() {
            Long nonExistentUserId = 999L;

            when(userRepository.findById(nonExistentUserId)).thenReturn(Optional.empty());

            NotFoundException exception = assertThrows(NotFoundException.class, () -> {
                service.getUserItems(nonExistentUserId);
            });

            assertEquals("Не удалось найти пользователя с id 999", exception.getMessage());

            verify(userRepository).findById(nonExistentUserId);
        }
    }

    @Nested
    class SearchItemsTest {

        @Test
        void searchItemsWhenTextIsEmpty() {
            when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));

            List<ItemDto> result = service.searchItems(user1.getId(), "");

            assertNotNull(result);
            assertTrue(result.isEmpty());

            verify(userRepository, times(1)).findById(user1.getId());
            verify(repository, never()).searchItems(any());
        }

        @Test
        void searchItemsWithNotEmpty() {
            String query = "item";
            when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));
            when(repository.searchItems(query)).thenReturn(List.of(item1, item2));

            List<ItemDto> result = service.searchItems(user1.getId(), query);

            assertEquals(2, result.size());
            checkItem(mapper.mapItemDto(item1), result.get(0));
            checkItem(mapper.mapItemDto(item2), result.get(1));

            verify(userRepository, times(1)).findById(user1.getId());
            verify(repository, times(1)).searchItems(query);
        }

        @Test
        void searchItemsWhenUserNotFound() {
            when(userRepository.findById(99L)).thenReturn(Optional.empty());

            NotFoundException ex = assertThrows(NotFoundException.class, () -> service.searchItems(99L, "item"));

            assertEquals("Не удалось найти пользователя с id 99", ex.getMessage());
            verify(repository, never()).searchItems(any());
        }
    }

    @Test
    void toItemWithCommentDto_shouldMapCorrectly() {
        User user = new User(1L, "name", "email@mail.com");
        Item item = Item.builder()
                .id(1L)
                .name("Item name")
                .description("Desc")
                .available(true)
                .owner(user)
                .build();

        CommentDto comment = CommentDto.builder()
                .id(1L)
                .text("Nice")
                .authorName("author")
                .build();

        BookingDto lastBooking = BookingDto.builder().id(10L).build();
        BookingDto nextBooking = BookingDto.builder().id(20L).build();

        ItemWithCommentDto dto = mapper.toItemWithCommentDto(
                item, List.of(comment), nextBooking, lastBooking);

        assertEquals(item.getId(), dto.getId());
        assertEquals(item.getName(), dto.getName());
        assertEquals(item.getDescription(), dto.getDescription());
        assertEquals(item.getAvailable(), dto.getAvailable());
        assertEquals(item.getOwner().getId(), dto.getOwner());
        assertEquals(1, dto.getComments().size());
        assertEquals(lastBooking, dto.getLastBooking());
        assertEquals(nextBooking, dto.getNextBooking());
    }
}
