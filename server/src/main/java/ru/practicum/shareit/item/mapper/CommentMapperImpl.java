package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Component
public class CommentMapperImpl implements CommentMapper {

    @Override
    public CommentResponseDto commentToCommentResponse(Comment comment) {
        return CommentResponseDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }

    @Override
    public Comment commentCreateDtoToComment(CommentCreateDto dto, User author, Item item, LocalDateTime created) {
        Comment comment = new Comment();
        comment.setAuthor(author);
        comment.setText(dto.getText());
        comment.setItem(item);
        comment.setCreated(created);
        return comment;
    }
}
