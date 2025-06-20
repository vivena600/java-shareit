package ru.practicum.shareit.request;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.FullRequestDto;
import ru.practicum.shareit.request.dto.RequestAddDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.service.RequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final RequestService requestService;

    /**
     * Создание нового запроса.
     * Post /requests
     * Headers X-Sharer-User-Id
     */
    @PostMapping
    public RequestDto createRequest(@RequestBody RequestAddDto request,
                                    @RequestHeader("X-Sharer-User-Id") Long userId) {
        return requestService.createRequest(request, userId);
    }

    /**
     * Получение информации о запросах пользователя и ответы на них
     * Post /requests
     * Headers X-Sharer-User-Id
     */
    @GetMapping
    public List<FullRequestDto> getRequestsByUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return null;
    }

    /**
     * Получение запросов, созданных другим пользователем
     * GET /requests/all
     * Headers X-Sharer-User-Id
     */
    @GetMapping("/all")
    public List<FullRequestDto> getRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return null;
    }

    /**
     * Получение информации о конкретном запросе и ответы на него
     * GET /requests/{requestId}
     * Headers X-Sharer-User-Id
     */
    @GetMapping("/{requestId}")
    public FullRequestDto getRequest(@PathVariable @Positive Long requestId,
                                     @RequestHeader("X-Sharer-User-Id") Long userId) {
        return null;
    }
}
