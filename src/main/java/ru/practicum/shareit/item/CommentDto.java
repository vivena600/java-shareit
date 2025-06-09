package ru.practicum.shareit.item;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentDto {
    private Long id;
    private String text;
    private Long author;
    private Long item;
    private LocalDateTime createdAt;
}
