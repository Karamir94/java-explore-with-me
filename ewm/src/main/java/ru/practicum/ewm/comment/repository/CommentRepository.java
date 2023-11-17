package ru.practicum.ewm.comment.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.comment.entity.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByReplyToIdId(Long replyToId);

    Page<Comment> findAllByAuthorId(Long author, Pageable pageable);

    Page<Comment> findAllByEventId(Long eventId, Pageable pageable);

    Integer countAllByEventId(Long eventId);
}
