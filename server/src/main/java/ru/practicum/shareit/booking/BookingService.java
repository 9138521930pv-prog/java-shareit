package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.State;

import java.util.List;

public interface BookingService {
    BookingDtoResponse addBooking(Integer userId, BookingDtoRequest bookingDtoInput);

    BookingDtoResponse setApprove(Integer bookingId, Integer userId, Boolean isApproved);

    BookingDtoResponse getBooking(Integer bookingId);

    List<BookingDtoResponse> getAllBooking(State bookingState, Integer userId);

    List<BookingDtoResponse> getAllBookingByOwner(State bookingState, Integer userId);
}