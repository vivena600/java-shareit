package ru.practicum.shareit.item;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.User;

@Data
@Builder
public class Item {
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    private String available;
    private User owner;
    private String request;
}
