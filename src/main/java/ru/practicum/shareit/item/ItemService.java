package ru.practicum.shareit.item;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

@Service
interface ItemService {
    ItemDto addItem(ItemDto itemDtoRequest, Integer userId);

    ItemDto updateItem(ItemDto itemDtoRequest, Integer userId, Integer itemId);

    ItemWithBookingAndCommentsDto getItem(Integer itemId, Integer userId);

    List<ItemWithBookingAndCommentsDto> getOwnerItems(Integer ownerId);

    List<ItemDto> itemSearch(Integer userId, String text);

    CommentDtoResponse addComment(Integer itemId, Integer userId, Comment text);
}