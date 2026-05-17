package ru.practicum.shareit.item;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoResponse;
import ru.practicum.shareit.item.model.Comment;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "text", target = "text")
    @Mapping(source = "author.name", target = "authorName")
    @Mapping(source = "created", target = "created")
    CommentDtoResponse mapToCommentDtoResponse(Comment comment);

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "text", target = "text"),
            @Mapping(source = "item.name", target = "itemName"),
            @Mapping(source = "author.name", target = "authorName"),
            @Mapping(source = "created", target = "created")
    })
    CommentDto mapToCommentDto(Comment comment);

    @Mapping(source = "text", target = "text")
    Comment mapToComment(CommentDto commentDto);
}
/*package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoResponse;
import ru.practicum.shareit.item.model.Comment;

@Component
@AllArgsConstructor
public class CommentMapper {
    public CommentDto mapToCommentDto(Comment comment) {
        return new CommentDto(comment.getId(), comment.getText(), comment.getItem().getName(),
                comment.getAuthor().getName(), comment.getCreated());
    }

    public Comment mapToComment(CommentDto commentDto) {
        Comment comment = new Comment();

        comment.setText(commentDto.getText());

        return comment;
    }
}

 */