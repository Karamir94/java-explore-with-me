package ru.practicum.ewm.comment.service;

import ru.practicum.ewm.comment.dto.CommentDtoRequest;
import ru.practicum.ewm.comment.dto.CommentDtoResponse;
import ru.practicum.ewm.comment.dto.CommentDtoResponseLong;
import ru.practicum.ewm.comment.dto.CommentDtoUpdateRequest;
import ru.practicum.ewm.event.entity.Event;

import java.util.List;
import java.util.Map;

public interface CommentService {

    void deleteCommentByAdmin(Long commentId);

    CommentDtoResponse updateCommentByAdmin(Long commentId, CommentDtoUpdateRequest request);

    List<CommentDtoResponseLong> searchUserCommentsByAdmin(Long userId, int from, int size);

    CommentDtoResponse createCommentByUser(Long userId, Long eventId, CommentDtoRequest request);

    CommentDtoResponse updateCommentByUser(Long userId, Long commentId, CommentDtoUpdateRequest request);

    void deleteCommentByUser(Long userId, Long commentId);

    List<CommentDtoResponse> getEventCommentsByUser(Long userId, Long eventId, int from, int size);

    Map<Long, Integer> getCommentsCountForEvents(List<Event> events);
}
