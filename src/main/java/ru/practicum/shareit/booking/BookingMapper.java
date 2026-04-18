package ru.practicum.shareit.booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.dto.BookingDtoShortResmonse;
import ru.practicum.shareit.booking.model.Booking;



@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BookingMapper {
    BookingMapper INSTANCE = Mappers.getMapper(BookingMapper.class);

    Booking mapToBooking(BookingDtoRequest bookingDtoInput);

    BookingDtoResponse mapToBookingDto(Booking booking);

    @Mapping(target = "bookerId", source = "booking.booker.id")
    BookingDtoShortResmonse mapToBookingDtoShort(Booking booking);
}