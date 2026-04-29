package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDtoShortResmonse;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemWithBookingAndCommentsDto extends ItemDto {
    private List<CommentDtoResponse> comments = new ArrayList<>();
    private BookingDtoShortResmonse lastBooking;
    private BookingDtoShortResmonse nextBooking;

    public ItemWithBookingAndCommentsDto(ItemDto itemDto, List<CommentDtoResponse> comments) {
        super(itemDto.getId(), itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable());
        this.comments = comments;
    }
}