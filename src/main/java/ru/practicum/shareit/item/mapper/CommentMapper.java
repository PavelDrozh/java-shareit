package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

public interface CommentMapper {

    CommentResponseDto commentToCommentResponse(Comment comment);

    Comment commentCreateDtoToComment(CommentCreateDto dto, User author, Item item, LocalDateTime created);
}
