package ru.practicum.shareit.item;

import org.mapstruct.*;
import ru.practicum.shareit.booking.dto.BookingDtoWithDate;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithComments;
import ru.practicum.shareit.item.dto.ItemWithBookingAndCommentsDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Mapper(componentModel = "spring", uses = CommentMapper.class)
public interface ItemMapper {

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "name", target = "name"),
            @Mapping(source = "available", target = "available"),
            @Mapping(source = "description", target = "description"),
            @Mapping(source = "requestId", target = "requestId")
    })
    ItemDto mapToItemDto(Item item);

    List<ItemDto> mapToItemDto(List<Item> items);

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "name", target = "name"),
            @Mapping(source = "available", target = "available"),
            @Mapping(source = "description", target = "description"),
            @Mapping(source = "requestId", target = "requestId"),
            @Mapping(target = "comments", ignore = true)
    })
    ItemDtoWithComments mapToItemDtoWithComments(Item item);

    @AfterMapping
    default void setNullBookings(Item item, @MappingTarget ItemDtoWithComments dto) {
        dto.setLastBooking(null);
        dto.setNextBooking(null);
    }

    @Mappings({
            @Mapping(source = "itemDto.id", target = "id"),
            @Mapping(source = "owner.id", target = "owner.id"),  // исправлено: маппинг в owner.id
            @Mapping(source = "itemDto.name", target = "name"),
            @Mapping(source = "itemDto.description", target = "description"),
            @Mapping(source = "itemDto.available", target = "available"),
            @Mapping(source = "itemDto.requestId", target = "requestId")
    })
    Item mapToItem(ItemDto itemDto, User owner);

    @Mappings({
            @Mapping(source = "item.id", target = "id"),
            @Mapping(source = "item.owner.id", target = "ownerId"),  // маппинг из item.owner.id в ownerId
            @Mapping(source = "item.name", target = "name"),
            @Mapping(source = "item.description", target = "description"),
            @Mapping(source = "item.available", target = "available"),
            @Mapping(source = "item.requestId", target = "requestId"),
            @Mapping(source = "last", target = "lastBooking"),
            @Mapping(source = "next", target = "nextBooking"),
            @Mapping(source = "commentsDtos", target = "comments")
    })
    ItemWithBookingAndCommentsDto mapToItemWithBookingDateAndCommentsDto(
            Item item,
            BookingDtoWithDate last,
            BookingDtoWithDate next,
            List<CommentDto> commentsDtos
    );
}

/*package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDtoWithDate;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithComments;
import ru.practicum.shareit.item.dto.ItemWithBookingAndCommentsDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Component
@AllArgsConstructor
public class ItemMapper {
    private final CommentMapper commentMapper;
    private final CommentService commentService;

    public ItemDto mapToItemDto(Item item) {
        ItemDto itemDto = new ItemDto();

        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setAvailable(item.getAvailable());
        itemDto.setDescription(item.getDescription());
        itemDto.setRequestId(item.getRequestId());

        return itemDto;
    }

    public List<ItemDto> mapToItemDto(List<Item> items) {
        return items.stream().map(item -> mapToItemDto(item)).toList();
    }

    public ItemDtoWithComments mapToItemDtoWithComments(Item item) {
        List<Comment> comments = commentService.findAllByItemId(item.getId());
        List<CommentDto> commentsDtos = comments.stream()
                .map(comment -> commentMapper.mapToCommentDto(comment)).toList();

        ItemDtoWithComments itemDto = new ItemDtoWithComments();

        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setAvailable(item.getAvailable());
        itemDto.setDescription(item.getDescription());
        itemDto.setRequestId(item.getRequestId());
        itemDto.setComments(commentsDtos);
        itemDto.setLastBooking(null);
        itemDto.setNextBooking(null);

        return itemDto;
    }

    public Item mapToItem(ItemDto itemDto, User owner) {
        Item item = new Item();

        item.setId(itemDto.getId());
        item.setOwner(owner);
        item.setName(itemDto.getName());
        item.setAvailable(itemDto.getAvailable());
        item.setDescription(itemDto.getDescription());
        item.setRequestId(itemDto.getRequestId());

        return item;
    }

    public ItemWithBookingAndCommentsDto mapToItemWithBookingDateAndCommentsDto(Item item, BookingDtoWithDate last,
                                                                                BookingDtoWithDate next,
                                                                                List<CommentDto> commentsDtos) {
        return new ItemWithBookingAndCommentsDto(item.getId(), item.getOwner().getId(), item.getName(),
                item.getDescription(), item.getAvailable(), item.getRequestId(), last, next, commentsDtos);
    }
}

 */