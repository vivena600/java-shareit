package ru.practicum.shareit.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@Data
@Builder
public class ItemRequest {
    private Long id;

    @NotBlank
    private String description;

    private User requestor;
    private LocalDateTime created;
}
