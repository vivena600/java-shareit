package shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ShortItemDto;
import ru.practicum.shareit.request.*;
import ru.practicum.shareit.request.dto.FullRequestDto;
import ru.practicum.shareit.request.dto.RequestAddDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.service.RequestServiceImpl;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestServiceImplTest {
    private final LocalDateTime testTime = LocalDateTime.of(2025, 07, 25, 00, 00, 00);

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RequestMapper requestMapper;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemMapper itemMapper;

    @InjectMocks
    private RequestServiceImpl requestService;

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

    private final ShortItemDto item1 = ShortItemDto.builder()
            .id(1L)
            .name("item1")
            .description("desc1")
            .build();

    private final ShortItemDto item2 = ShortItemDto.builder()
            .id(2L)
            .name("item2")
            .description("desc2")
            .build();

    private final FullRequestDto fullRequest = FullRequestDto.builder()
            .id(request.getId())
            .description(request.getDescription())
            .created(request.getCreated())
            .items(Arrays.asList(item1, item2))
            .build();

    @Test
    void getRequestById() {
        when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));
        when(requestRepository.findById(request.getId())).thenReturn(Optional.of(request));
        when(itemRepository.findByRequest_Id(request.getId())).thenReturn(Collections.emptyList());
        when(itemMapper.toShortItemDtoList(Collections.emptyList())).thenReturn(Collections.emptyList());
        when(requestMapper.mapFullRequestDto(request, Collections.emptyList())).thenReturn(fullRequest);

        FullRequestDto result = requestService.getRequestById(request.getId(), user1.getId());

        assertNotNull(result);
        assertEquals(fullRequest.getId(), result.getId());
        assertEquals(fullRequest.getDescription(), result.getDescription());
        assertEquals(fullRequest.getItems().size(), result.getItems().size());

        verify(userRepository).findById(user1.getId());
        verify(requestRepository).findById(request.getId());
        verify(itemRepository).findByRequest_Id(request.getId());
        verify(requestMapper).mapFullRequestDto(request, Collections.emptyList());
    }

    @Test
    void getRequestByIdWithFailUser() {
        when(userRepository.findById(user1.getId())).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> requestService.getRequestById(request.getId(), user1.getId()));

        assertEquals("Не удалось найти пользователя с id " + request.getId(), ex.getMessage());
        verify(userRepository).findById(request.getId());
        verifyNoInteractions(requestRepository);
    }

    @Test
    void getRequestByIdRequestNotFound() {
        when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));
        when(requestRepository.findById(request.getId())).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> requestService.getRequestById(request.getId(), request.getId()));

        assertEquals("Не удалось найти запрос с id " + request.getId(), ex.getMessage());
        verify(userRepository).findById(request.getId());
        verify(requestRepository).findById(request.getId());
    }

    @Test
    void createRequest() {
        RequestAddDto addDto = new RequestAddDto();
        addDto.setDescription("New request");

        RequestDto requestDto = RequestDto.builder()
                .description("New request")
                .userId(user1.getId())
                .created(testTime)
                .build();

        ItemRequest entity = ItemRequest.builder()
                .description(addDto.getDescription())
                .requester(user1)
                .created(testTime)
                .build();

        when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));
        when(requestMapper.mapItemRequest(any(RequestDto.class), eq(user1))).thenReturn(entity);
        when(requestRepository.save(entity)).thenReturn(entity);
        when(requestMapper.mapRequestDto(entity)).thenReturn(requestDto);

        RequestDto result = requestService.createRequest(addDto, user1.getId());

        assertNotNull(result);
        assertEquals(addDto.getDescription(), result.getDescription());
        assertEquals(user1.getId(), result.getUserId());

        verify(userRepository).findById(user1.getId());
        verify(requestMapper).mapItemRequest(any(RequestDto.class), eq(user1));
        verify(requestRepository).save(entity);
        verify(requestMapper).mapRequestDto(entity);
    }

    @Test
    void createRequestUserNotFound() {
        RequestAddDto addDto = new RequestAddDto();
        addDto.setDescription("New request");

        when(userRepository.findById(user1.getId())).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> requestService.createRequest(addDto, user1.getId()));

        assertEquals("Не удалось найти пользователя с id " + user1.getId(), ex.getMessage());
    }

    @Test
    void getAllRequests() {
        when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));
        when(requestRepository.findByNotUserId(user1.getId())).thenReturn(Collections.singletonList(request));
        when(requestMapper.mapRequestDto(Collections.singletonList(request))).thenReturn(Collections.singletonList(RequestDto.builder()
                .description(request.getDescription())
                .userId(user1.getId())
                .created(request.getCreated())
                .build()));

        var result = requestService.getAllRequests(user1.getId());

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(request.getDescription(), result.get(0).getDescription());

        verify(userRepository).findById(user1.getId());
        verify(requestRepository).findByNotUserId(user1.getId());
        verify(requestMapper).mapRequestDto(Collections.singletonList(request));
    }

    @Test
    void getAllRequestsUserNotFound() {
        when(userRepository.findById(user1.getId())).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> requestService.getAllRequests(user1.getId()));

        assertEquals("Не удалось найти пользователя с id " + user1.getId(), ex.getMessage());
    }

    @Test
    void getRequestByUser() {
        when(requestRepository.findByRequester_IdOrderByCreatedDesc(user1.getId()))
                .thenReturn(Collections.singletonList(request));
        when(itemRepository.findByRequest_Id(request.getId())).thenReturn(Collections.emptyList());
        when(itemMapper.toShortItemDtoList(Collections.emptyList())).thenReturn(Collections.emptyList());
        when(requestMapper.mapFullRequestDto(request, Collections.emptyList())).thenReturn(fullRequest);

        var results = requestService.getRequestByUser(user1.getId());

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(fullRequest.getId(), results.get(0).getId());

        verify(requestRepository).findByRequester_IdOrderByCreatedDesc(user1.getId());
        verify(itemRepository).findByRequest_Id(request.getId());
        verify(requestMapper).mapFullRequestDto(request, Collections.emptyList());
    }
}
