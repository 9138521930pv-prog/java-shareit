
package ru.practicum.shareit.booking;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.dto.BookingDtoWithDate;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class, ItemMapper.class})
public interface BookingMapper {

    @Mappings({
            @Mapping(source = "bookingDto.id", target = "id"),
            @Mapping(source = "bookingDto.start", target = "start"),
            @Mapping(source = "bookingDto.end", target = "end"),
            @Mapping(source = "item", target = "item"),
            @Mapping(source = "booker", target = "booker"),
            @Mapping(source = "bookingDto.status", target = "status")
    })
    Booking mapToBooking(BookingDtoRequest bookingDto, User booker, Item item);

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "start", target = "start"),
            @Mapping(source = "end", target = "end"),
            @Mapping(source = "item", target = "item"),  // исправлено: было itemDto
            @Mapping(source = "booker", target = "booker"),  // исправлено: было bookerDto
            @Mapping(source = "status", target = "status")
    })
    BookingDtoResponse mapToDtoResponse(Booking booking);

    List<BookingDtoResponse> mapToDtoResponse(List<Booking> bookings);

    @Mappings({
            @Mapping(source = "start", target = "start"),
            @Mapping(source = "end", target = "end")
    })
    BookingDtoWithDate mapToBookingDtoWithDate(Booking booking);
}
/*package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.dto.BookingDtoWithDate;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Component
@AllArgsConstructor
public class BookingMapper {

    private final UserMapper userMapper;
    private final ItemMapper itemMapper;

    public Booking mapToBooking(BookingDtoRequest bookingDto, User booker, Item item) {
        return new Booking(bookingDto.getId(), bookingDto.getStart(), bookingDto.getEnd(),
                item, booker, bookingDto.getStatus());
    }

    public BookingDtoResponse mapToDtoResponse(Booking booking) {
        return new BookingDtoResponse(booking.getId(), booking.getStart(), booking.getEnd(),
                itemMapper.mapToItemDto(booking.getItem()),
                userMapper.mapToUserDto(booking.getBooker()), booking.getStatus());
    }

    public List<BookingDtoResponse> mapToDtoResponse(List<Booking> bookings) {
        return bookings.stream().map(booking -> mapToDtoResponse(booking)).toList();
    }

    public BookingDtoWithDate mapToBookingDtoWithDate(Booking booking) {
        return new BookingDtoWithDate(booking.getStart(), booking.getEnd());
    }
}

 */