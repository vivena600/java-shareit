package shareit.request;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.dto.ShortItemDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.RequestMapperImpl;
import ru.practicum.shareit.request.dto.FullRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class RequestMapperImplTest {

    private final LocalDateTime dateTime = LocalDateTime.of(2023, 1, 1, 10, 0, 0);

    private final User user = User.builder()
            .id(1L)
            .name("Test user 1")
            .email("tester1@ya.ru")
            .build();

    private final ItemRequest itemRequest = ItemRequest.builder()
            .id(1L)
            .description("itemRequest1 description")
            .requester(user)
            .created(dateTime)
            .build();

    private final RequestDto requestDto = RequestDto.builder()
            .id(1L)
            .description("itemRequest1 description")
            .userId(user.getId())
            .created(dateTime)
            .build();

    private final List<ShortItemDto> itemsDto = List.of(
            ShortItemDto.builder()
                    .id(1L)
                    .name("item name")
                    .description("item description")
                    .userId(user.getId())
                    .build()
    );

    @InjectMocks
    private RequestMapperImpl requestMapper;

    @Nested
    class MapRequestDto {
        @Test
        public void shouldMapItemRequestToRequestDto() {
            RequestDto result = requestMapper.mapRequestDto(itemRequest);

            assertEquals(itemRequest.getId(), result.getId());
            assertEquals(itemRequest.getDescription(), result.getDescription());
            assertEquals(itemRequest.getCreated(), result.getCreated());
            assertEquals(itemRequest.getRequester().getId(), result.getUserId());
        }
    }

    @Nested
    class MapItemRequest {
        @Test
        public void shouldMapRequestDtoToItemRequest() {
            ItemRequest result = requestMapper.mapItemRequest(requestDto, user);

            assertEquals(requestDto.getId(), result.getId());
            assertEquals(user, result.getRequester());
            assertEquals(requestDto.getDescription(), result.getDescription());
            assertEquals(requestDto.getCreated(), result.getCreated());
        }
    }

    @Nested
    class MapFullRequestDto {
        @Test
        public void shouldMapToFullRequestDto() {
            FullRequestDto result = requestMapper.mapFullRequestDto(itemRequest, itemsDto);

            assertEquals(itemRequest.getId(), result.getId());
            assertEquals(itemRequest.getDescription(), result.getDescription());
            assertEquals(itemRequest.getCreated(), result.getCreated());
            assertEquals(itemRequest.getRequester().getId(), result.getUserId());
            assertEquals(itemsDto, result.getItems());
        }
    }

    @Nested
    class MapRequestDtoList {
        @Test
        public void shouldMapListOfItemRequestsToListOfRequestDtos() {
            List<RequestDto> result = requestMapper.mapRequestDto(List.of(itemRequest));

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(itemRequest.getId(), result.get(0).getId());
        }
    }
}
