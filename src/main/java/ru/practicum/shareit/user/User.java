package ru.practicum.shareit.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {
    private Long id;

    @NotBlank(message = "Не указано имя пользователя")
    private String name;

    @Email(message = "Не корректная почта")
    @NotBlank(message = "Не указан email пользователя")
    private String email;
}
