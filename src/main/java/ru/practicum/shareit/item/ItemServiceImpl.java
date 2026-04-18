package ru.practicum.shareit.item;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemMapper itemMapper;
    private final BookingMapper bookingMapper;
    private final CommentMapper commentMapper;

    private void validateUser(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Failed to process request. User with id = " + userId + " doesn't exist.");
        }
    }

    private void validateItem(Integer itemId) {
        if (!itemRepository.existsById(itemId)) {
            throw new NotFoundException("Failed to process request. Item with id = " + itemId + " doesn't exist.");
        }
    }

    private void validateItemDto(ItemDto itemDto) {
        if (itemDto.getAvailable() == null || itemDto.getName() == null || itemDto.getName().isEmpty()
                || itemDto.getDescription() == null) {
            throw new ValidationException("Failed to process request. Item's name, description or isAvailable status must not be null.");
        }
    }
    @Override
    @Transactional
    public ItemDto addItem(ItemDto itemDtoRequest, Integer userId){
        validateItemDto(itemDtoRequest);
        validateUser(userId);

        Item item = itemMapper.mapToItem(itemDtoRequest);
        item.setOwner(userRepository.findById(userId).get());
        item = itemRepository.save(item);
        return itemMapper.mapToItemDto(item);
    }

    @Override
    @Transactional
        public ItemDto updateItem(ItemDto itemDtoRequest, Integer userId, Integer itemId) {
            validateItem(itemId);

        if (itemId.equals(itemDtoRequest.getId())) {
            validateUser(userId);
        }

        Item item = itemRepository.findById(itemId).get();

        if (!Objects.equals(userId, item.getOwner().getId())) {
            throw new ItemAccessException("Failed to update item. Only item owners are allowed to update items.");
        }

        if (itemDtoRequest.getName() != null) {
            item.setName(itemDtoRequest.getName());
        }

        if (itemDtoRequest.getDescription() != null) {
            item.setDescription(itemDtoRequest.getDescription());
        }

        if (itemDtoRequest.getAvailable() != null) {
            item.setAvailable(itemDtoRequest.getAvailable());
        }

        item = itemRepository.save(item);
        return itemMapper.mapToItemDto(item);
    }

    @Override
    @Transactional
    public ItemWithBookingAndCommentsDto getItem(Integer itemId, Integer userId) {
        validateItem(itemId);

        Sort sort = Sort.by("start").descending();

        Item item = itemRepository.findById(itemId).get();
        ItemDto itemDto = itemMapper.mapToItemDto(item);

        List<CommentDtoResponse> itemComments = commentRepository.findByItemId(itemId).stream()
                .map(commentMapper::mapToCommentDtoResponse).collect(Collectors.toList());

        ItemWithBookingAndCommentsDto itemDtoResp = new ItemWithBookingAndCommentsDto(itemDto, itemComments);

        if (Objects.equals(userId, item.getOwner().getId())) {
            List<Booking> last = bookingRepository.findByItemIdAndStartIsBeforeAndStatusNot(itemId, LocalDateTime.now(), Status.REJECTED, sort);
            if (!last.isEmpty()) {
                Booking lastBooking = last.get(0);
                itemDtoResp.setLastBooking(bookingMapper.mapToBookingDtoShort(lastBooking));
            }

            List<Booking> next = bookingRepository.findByItemIdAndStartIsAfterAndStatusNot(itemId, LocalDateTime.now(), Status.REJECTED, sort.ascending());
            if (!next.isEmpty()) {
                Booking nextBooking = next.get(0);
                itemDtoResp.setNextBooking(bookingMapper.mapToBookingDtoShort(nextBooking));
            }
        }
        return itemDtoResp;
    }

    @Override
    public List<ItemWithBookingAndCommentsDto> getOwnerItems(Integer userId) {
        Sort sort = Sort.by("start").descending();

        List<ItemWithBookingAndCommentsDto> userItems = itemRepository.findByOwnerId(userId).stream()
                .map(itemMapper::mapToItemDto)
                .map(itemDto -> new ItemWithBookingAndCommentsDto(itemDto, null))
                .map(item -> {
                    List<Booking> last = bookingRepository.findByItemIdAndStartIsBeforeAndStatusNot(item.getId(), LocalDateTime.now(), Status.REJECTED, sort);
                    if (!last.isEmpty()) {
                        Booking lastBooking = last.get(0);
                        item.setLastBooking(bookingMapper.mapToBookingDtoShort(lastBooking));
                    }

                    List<Booking> next = bookingRepository.findByItemIdAndStartIsAfterAndStatusNot(item.getId(), LocalDateTime.now(), Status.REJECTED, sort.ascending());
                    if (!next.isEmpty()) {
                        Booking nextBooking = next.get(0);
                        item.setNextBooking(bookingMapper.mapToBookingDtoShort(nextBooking));
                    }
                    return item;
                }).collect(Collectors.toList());

        return userItems;
    }

    @Override
    @Transactional
    public List<ItemDto> itemSearch(Integer userId, String text) {
        if (text == null || text.isEmpty()) {
            return new ArrayList<>();
        }
        String searchQuery = text.toLowerCase();
        return itemRepository.findAll().stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(searchQuery)
                        || item.getDescription().toLowerCase().contains(searchQuery))
                .map(itemMapper::mapToItemDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDtoResponse addComment(Integer itemId, Integer userId, Comment commentInput) {
        validateUser(userId);
        validateItem(itemId);

        Optional<Item> optionalItem = itemRepository.findById(itemId);
        if ((optionalItem.isEmpty())) {
            throw new NotFoundException("Booking with id = " + itemId + " was not found.");
        }
        Item item = optionalItem.get();

        Sort sort = Sort.by("start").descending();

        List<Booking> bookings = bookingRepository.findByBooker_IdAndEndIsBefore(userId, LocalDateTime.now(), sort);
        if (bookings.isEmpty()) {
            throw new UnavailableItemBookingException("Failed to add comment. No finished bookings found.");
        }

        if (item.getOwner().getId().equals(userId)) {
            throw new IllegalItemBookingException("Failed to add comment. Item owners are not allowed to comment on the booking of their items.");
        }

        commentInput.setItem(item);
        commentInput.setAuthor(userRepository.findById(userId).get());

        Comment comment = commentRepository.save(commentInput);

        return commentMapper.mapToCommentDtoResponse(comment);
    }
}