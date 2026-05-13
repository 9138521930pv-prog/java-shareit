package ru.practicum.shareit.item;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

@Service
interface ItemService {
    ItemDto addItem(ItemDto itemDtoRequest, Integer userId);

    ItemDto updateItem(ItemDtoUpdate itemDtoRequest, Integer userId, Integer itemId);

    ItemDtoWithComments getItem(Integer itemId);

    List<ItemWithBookingAndCommentsDto> getOwnerItems(Integer ownerId);

    List<ItemDto> itemSearch(String text);

    CommentDto addComment(Integer itemId, Integer userId, CommentDto text);

}