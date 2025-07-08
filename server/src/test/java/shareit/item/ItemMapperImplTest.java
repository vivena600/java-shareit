package shareit.item;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.ItemMapperImpl;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithCommentDto;
import ru.practicum.shareit.item.dto.ShortItemDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.item.Item;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ItemMapperImplTest {

    private final User user = User.builder()
            .id(1L)
            .name("Test User")
            .email("user@test.com")
            .build();

    private final ItemRequest itemRequest = ItemRequest.builder()
            .id(10L)
            .description("Request description")
            .requester(user)
            .created(LocalDateTime.now())
            .build();

    private final ItemDto itemDto = ItemDto.builder()
            .id(100L)
            .name("Item name")
            .description("Item description")
            .available(true)
            .owner(user.getId())
            .requestId(itemRequest.getId())
            .build();

    private final Item item = Item.builder()
            .id(100L)
            .name("Item name")
            .description("Item description")
            .available(true)
            .owner(user)
            .request(itemRequest)
            .build();

    private final BookingDto nextBooking = BookingDto.builder()
            .id(1L)
            .build();

    private final BookingDto lastBooking = BookingDto.builder()
            .id(2L)
            .build();

    private final List<CommentDto> comments = List.of(
            CommentDto.builder()
                    .id(1L)
                    .text("Comment text")
                    .authorName("Author")
                    .build()
    );

    @InjectMocks
    private ItemMapperImpl itemMapper;

    @Nested
    class MapItem {
        @Test
        public void shouldMapItemDtoToItem() {
            Item result = itemMapper.mapItem(itemDto, user, itemRequest);

            assertEquals(itemDto.getId(), result.getId());
            assertEquals(itemDto.getName(), result.getName());
            assertEquals(itemDto.getDescription(), result.getDescription());
            assertEquals(itemDto.getAvailable(), result.getAvailable());
            assertEquals(user, result.getOwner());
            assertEquals(itemRequest, result.getRequest());
        }
    }

    @Nested
    class MapItemDto {
        @Test
        public void shouldMapItemToItemDto() {
            ItemDto result = itemMapper.mapItemDto(item);

            assertEquals(item.getId(), result.getId());
            assertEquals(item.getName(), result.getName());
            assertEquals(item.getDescription(), result.getDescription());
            assertEquals(item.getAvailable(), result.getAvailable());
            assertEquals(user.getId(), result.getOwner());
            assertEquals(itemRequest.getId(), result.getRequestId());
        }

        @Test
        public void shouldHandleNullOwnerAndRequest() {
            Item itemWithoutOwnerRequest = Item.builder()
                    .id(123L)
                    .name("Test")
                    .description("Test desc")
                    .available(false)
                    .build();

            ItemDto result = itemMapper.mapItemDto(itemWithoutOwnerRequest);

            assertEquals(123L, result.getId());
            assertEquals("Test", result.getName());
            assertEquals("Test desc", result.getDescription());
            assertFalse(result.getAvailable());
            assertNull(result.getOwner());
            assertNull(result.getRequestId());
        }
    }

    @Nested
    class ToItemWithCommentDto {
        @Test
        public void shouldMapItemWithCommentsAndBookings() {
            ItemWithCommentDto result = itemMapper.toItemWithCommentDto(item, comments, nextBooking, lastBooking);

            assertEquals(item.getId(), result.getId());
            assertEquals(item.getName(), result.getName());
            assertEquals(item.getDescription(), result.getDescription());
            assertEquals(comments, result.getComments());
            assertEquals(item.getAvailable(), result.getAvailable());
            assertEquals(user.getId(), result.getOwner());
            assertEquals(nextBooking, result.getNextBooking());
            assertEquals(lastBooking, result.getLastBooking());
        }
    }

    @Nested
    class ToShortItemDto {
        @Test
        public void shouldMapItemToShortItemDto() {
            ShortItemDto result = itemMapper.toShortItemDto(item);

            assertEquals(item.getId(), result.getId());
            assertEquals(item.getName(), result.getName());
            assertEquals(item.getDescription(), result.getDescription());
            assertEquals(user.getId(), result.getUserId());
        }

        @Test
        public void shouldHandleNullOwner() {
            Item itemWithoutOwner = Item.builder()
                    .id(200L)
                    .name("No Owner")
                    .description("No Owner Desc")
                    .available(true)
                    .build();

            ShortItemDto result = itemMapper.toShortItemDto(itemWithoutOwner);

            assertEquals(200L, result.getId());
            assertEquals("No Owner", result.getName());
            assertEquals("No Owner Desc", result.getDescription());
            assertNull(result.getUserId());
        }
    }

    @Nested
    class ToShortItemDtoList {
        @Test
        public void shouldMapListOfItemsToShortItemDtoList() {
            List<ShortItemDto> result = itemMapper.toShortItemDtoList(List.of(item));

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(item.getId(), result.get(0).getId());
        }

        @Test
        public void shouldReturnEmptyListForEmptyInput() {
            List<ShortItemDto> result = itemMapper.toShortItemDtoList(Collections.emptyList());

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        public void shouldReturnNullForNullInput() {
            List<ShortItemDto> result = itemMapper.toShortItemDtoList(null);
            assertNull(result);
        }
    }
}
