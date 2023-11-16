package ru.practicum.ewm.comment.mapper;

import org.mapstruct.Mapper;
import ru.practicum.ewm.comment.dto.CommentDtoRequest;
import ru.practicum.ewm.comment.dto.CommentDtoResponse;
import ru.practicum.ewm.comment.dto.CommentDtoResponseLong;
import ru.practicum.ewm.comment.entity.Comment;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    Comment toNewComment(CommentDtoRequest request);

    CommentDtoResponse toCommentDtoResponse(Comment result);

//    CommentDtoResponseLong toCommentDtoResponseLong(Comment result);

    List<CommentDtoResponseLong> toCommentDtosResponseLong(List<Comment> result);

    List<CommentDtoResponse> toCommentDtosResponse(List<Comment> result);
}
