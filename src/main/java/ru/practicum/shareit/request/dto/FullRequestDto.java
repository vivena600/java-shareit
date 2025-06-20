package ru.practicum.shareit.request.dto;

import java.time.LocalDateTime;
import java.util.List;

public class FullRequestDto {
    private Long id;
    private long userId;
    private String description;
    private LocalDateTime created;
    private List<ResponseRequestDto> response;
}
