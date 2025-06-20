package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.FullRequestDto;
import ru.practicum.shareit.request.dto.RequestAddDto;
import ru.practicum.shareit.request.dto.RequestDto;

import java.util.List;

public interface RequestService {

    RequestDto createRequest(RequestAddDto requestAddDto, Long userId);

    List<FullRequestDto> createFullRequest(Long userId);
}
