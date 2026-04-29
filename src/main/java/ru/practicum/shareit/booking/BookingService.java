package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;

import java.util.List;

public interface BookingService {
    BookingDtoResponse addBooking(Integer userId, BookingDtoRequest bookingDtoInput);

    BookingDtoResponse setApprove(Integer bookingId, Integer userId, Boolean isApproved);

    BookingDtoResponse getBooking(Integer bookingId, Integer userId);

    List<BookingDtoResponse> getAllBooking(String bookingState, Integer userId);

    List<BookingDtoResponse> getAllBookingByOwner(String bookingState, Integer userId);
}