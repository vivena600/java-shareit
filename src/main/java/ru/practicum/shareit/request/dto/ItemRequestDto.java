package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
@Builder
public class ItemRequestDto {
    private Long id;

    @NotBlank
    private String description;

    private User requestor;
    private LocalDateTime created;
}
