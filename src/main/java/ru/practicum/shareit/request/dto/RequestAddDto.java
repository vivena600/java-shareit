package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestAddDto {

    @NotNull
    private String description;
}
