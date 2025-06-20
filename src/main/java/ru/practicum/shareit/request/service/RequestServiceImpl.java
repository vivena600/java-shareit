package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.RequestMapper;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.request.dto.RequestAddDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
@Slf4j
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;

    private final RequestMapper requestMapper;

    @Override
    public RequestDto createRequest(RequestAddDto requestAddDto, Long userId) {
        log.info("Пользователь с id {} создает запрос", userId);
        RequestDto newRequest = RequestDto.builder()
                .description(requestAddDto.getDescription())
                .userId(userId)
                .created(LocalDateTime.now()).build();

        User user = checkUser(userId);
        ItemRequest entity = requestMapper.mapItemRequest(newRequest, user);
        return requestMapper.mapRequestDto(requestRepository.save(entity));
    }

    private User checkUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Не удалось найти пользователя с id " + userId));
    }
}
