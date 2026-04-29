package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
@Validated
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto addItem(@RequestHeader("X-Sharer-User-Id") Integer userId, @Valid @RequestBody ItemDto item) {
        return itemService.addItem(item, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable Integer itemId, @RequestBody ItemDto item,
                              @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemService.updateItem(item, userId, itemId);
    }

    @GetMapping("/{itemId}")
    public ItemWithBookingAndCommentsDto getItem(@PathVariable @NotNull @Positive Integer itemId,
                                                 @RequestHeader("X-Sharer-User-Id") @NotNull Integer userId) {
        return itemService.getItem(itemId, userId);
    }

    @GetMapping
    public List<ItemWithBookingAndCommentsDto> getOwnerItems(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemService.getOwnerItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> itemSearch(@RequestHeader("X-Sharer-User-Id") @NotNull Integer userId, @RequestParam("text") String text) {
        return itemService.itemSearch(userId, text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDtoResponse addComment(@PathVariable Integer itemId,
                                        @RequestHeader("X-Sharer-User-Id") Integer userId,
                                        @RequestBody Comment text) {
        return itemService.addComment(itemId, userId, text);
    }

}