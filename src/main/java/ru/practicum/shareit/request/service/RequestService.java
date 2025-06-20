package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.RequestAddDto;
import ru.practicum.shareit.request.dto.RequestDto;

public interface RequestService {

    RequestDto createRequest(RequestAddDto requestAddDto, Long userId);
}
