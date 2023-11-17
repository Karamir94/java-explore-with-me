package ru.practicum.ewm.comment.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.comment.dto.CommentDtoRequest;
import ru.practicum.ewm.comment.dto.CommentDtoResponse;
import ru.practicum.ewm.comment.dto.CommentDtoResponseLong;
import ru.practicum.ewm.comment.dto.CommentDtoUpdateRequest;
import ru.practicum.ewm.comment.entity.Comment;
import ru.practicum.ewm.comment.enums.CommentSort;
import ru.practicum.ewm.comment.enums.MessageUpdateInitiator;
import ru.practicum.ewm.comment.mapper.CommentMapper;
import ru.practicum.ewm.comment.repository.CommentRepository;
import ru.practicum.ewm.error.exception.AlreadyExistException;
import ru.practicum.ewm.error.exception.NotExistException;
import ru.practicum.ewm.event.entity.Event;
import ru.practicum.ewm.event.enums.EventState;
import ru.practicum.ewm.event.exception.EventNotPublishedException;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.user.entity.User;
import ru.practicum.ewm.user.exception.WrongUserException;
import ru.practicum.ewm.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Transactional
@Slf4j
@AllArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CommentMapper commentMapper;

    @Override
    public void deleteCommentByAdmin(Long commentId) {
        Comment comment = getComment(commentId);
        if (comment.getAnswered() != null) {
            List<Comment> messages = commentRepository.findAllByReplyToIdId(commentId);
            for (Comment cmt : messages) {
                cmt.setReplyToId(null);
            }
            commentRepository.saveAll(messages);
        }
        commentRepository.deleteById(commentId);
        log.info("Comment with ID {} was deleted by admin", commentId);
    }

    @Override
    public CommentDtoResponse updateCommentByAdmin(Long commentId, CommentDtoUpdateRequest request) {
        Comment comment = getComment(commentId);
        comment.setText(request.getText());
        comment.setLastUpdatedOn(LocalDateTime.now());
        comment.setUpdateInitiator(MessageUpdateInitiator.ADMIN);
        Comment result = commentRepository.save(comment);

        log.info("Comment with ID {} was updated by admin", commentId);
        return commentMapper.toCommentDtoResponse(result);
    }

    @Override
    public List<CommentDtoResponseLong> searchUserCommentsByAdmin(Long userId, int from, int size) {
        existsUser(userId);
        Sort sort = Sort.by(CommentSort.CREATED_ON.getTitle());
        Pageable pageable = PageRequest.of(from / size, size, sort);
        List<Comment> comments = commentRepository.findAllByAuthorId(userId, pageable).toList();

        log.info("{} messages was founded", comments.size());
        return commentMapper.toCommentDtosResponseLong(comments);
    }

    @Override
    public CommentDtoResponse createCommentByUser(Long userId, Long eventId, CommentDtoRequest request) {
        Event event = getEvent(eventId);
        if (event.getState() != EventState.PUBLISHED) {
            throw new EventNotPublishedException("only for a published event");
        }
        User user = getUser(userId);
        Comment reply = null;
        if (request.getReplyToIdLong() != null) {
            reply = getComment(request.getReplyToIdLong());
            if (reply.getAnswered() == null || !reply.getAnswered()) {
                reply.setAnswered(true);
                reply = commentRepository.save(reply);
            }
        }
        MessageUpdateInitiator initiator = MessageUpdateInitiator.USER;
        Comment result = commentMapper.toNewComment(request);

        result.setEvent(event);
        result.setAuthor(user);
        result.setReplyToId(reply);
        result.setUpdateInitiator(initiator);
        result.setCreatedOn(LocalDateTime.now());
        result = commentRepository.save(result);

        log.info("Create comment ID {} for event ID {}", result.getId(), result.getEvent().getId());
        return commentMapper.toCommentDtoResponse(result);
    }

    @Override
    public CommentDtoResponse updateCommentByUser(Long userId, Long commentId, CommentDtoUpdateRequest request) {
        Comment comment = getComment(commentId);
        existsUser(userId);
        if (!Objects.equals(comment.getAuthor().getId(), userId)) {
            throw new WrongUserException("User with id=" + userId + " is not the author of the comment");
        }
        if (comment.getAnswered() != null && comment.getAnswered()) {
            throw new AlreadyExistException("The comment has already been answered.");
        }

        comment.setText(request.getText());
        comment.setLastUpdatedOn(LocalDateTime.now());
        comment.setUpdateInitiator(MessageUpdateInitiator.USER);
        Comment result = commentRepository.save(comment);

        log.info("Comment with ID {} was updated by user", commentId);
        return commentMapper.toCommentDtoResponse(result);
    }

    @Override
    public void deleteCommentByUser(Long userId, Long commentId) {
        Comment comment = getComment(commentId);
        existsUser(userId);
        if (!Objects.equals(comment.getAuthor().getId(), userId)) {
            throw new WrongUserException("User with id=" + userId + " is not the author of the comment");
        }
        if (comment.getAnswered() != null && comment.getAnswered()) {
            throw new AlreadyExistException("The comment has already been answered.");
        }

        commentRepository.deleteById(commentId);
        log.info("Comment with ID {} was deleted by user", commentId);
    }

    @Override
    public List<CommentDtoResponse> getEventCommentsByUser(Long userId, Long eventId, int from, int size) {
        existsUser(userId);
        Sort sort = Sort.by(CommentSort.CREATED_ON.getTitle());
        Pageable pageable = PageRequest.of(from / size, size, sort);
        List<Comment> comments = commentRepository.findAllByEventId(eventId, pageable).toList();

        log.info("{} comments for event ID {} was founded", comments.size(), eventId);
        return commentMapper.toCommentDtosResponse(comments);
    }

    @Override
    public Map<Long, Integer> getCommentsCountForEvents(List<Event> events) {
        Map<Long, Integer> result = new HashMap<>();

        for (Event event : events) {
            Integer count = commentRepository.countAllByEventId(event.getId()) == null ?
                    0 : commentRepository.countAllByEventId(event.getId());
            result.put(event.getId(), count);
        }
        return result;
    }


    private void existsUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotExistException("User id = " + userId + " not found");
        }
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotExistException("User id = " + userId + " not found"));
    }

    private Comment getComment(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new NotExistException("Comment id=" + commentId + " not found"));
    }

    private Event getEvent(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotExistException("Event id=" + eventId + " not found"));
    }
}
