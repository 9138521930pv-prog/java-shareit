package ru.practicum.shareit.item;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;


import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto addItem(@RequestHeader("X-Sharer-User-Id") Integer userId, @RequestBody ItemDto item) {
        return itemService.addItem(item, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable Integer itemId, @RequestBody ItemDtoUpdate item,
                              @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemService.updateItem(item, userId, itemId);
    }

    @GetMapping("/{itemId}")
    public ItemDtoWithComments getItem(@PathVariable Integer itemId) {
        return itemService.getItem(itemId);
    }

    @GetMapping
    public List<ItemWithBookingAndCommentsDto> getOwnerItems(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemService.getOwnerItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> itemSearch(@RequestParam("text") String text) {
        return itemService.itemSearch(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@PathVariable Integer itemId,
                                 @RequestHeader("X-Sharer-User-Id") Integer userId,
                                 @RequestBody CommentDto text) {
        return itemService.addComment(itemId, userId, text);
    }

}