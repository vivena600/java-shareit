package ru.practicum.shareit.item;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

@Data
@Builder
public class Item {
    private Long id;

    @NotBlank(message = "Не заполнено наименование вещи")
    private String name;

    @NotBlank(message = "Не заполнено описание вещи")
    private String description;

    @NotNull(message = "Не заполнен статус аренды")
    private Boolean available;

    private User owner;
    private ItemRequest request;
}
