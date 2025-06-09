package ru.practicum.shareit.item.service;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class CommentMapper {

    public Comment mapComment(CommentDto comment, User user, Item item) {
        return Comment.builder()
                .id(comment.getId())
                .author(user)
                .text(comment.getText())
                .item(item)
                .createdAt(comment.getCreated())
                .build();
    }

    public CommentDto mapCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .authorName(comment.getAuthor().getName())
                .text(comment.getText())
                .created(comment.getCreatedAt())
                .build();
    }

    public List<CommentDto> mapListCommentDto(List<Comment> comments) {
        return comments.stream().map(CommentMapper::mapCommentDto).collect(Collectors.toList());
    }
}
