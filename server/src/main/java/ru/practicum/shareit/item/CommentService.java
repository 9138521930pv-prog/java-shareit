package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

@Service
@RequiredArgsConstructor

public class CommentService {
    private final CommentRepository commentRepository;

    public List<Comment> findAllByItemId(Integer itemId) {
        return commentRepository.findAllByItemId(itemId);
    }
}
