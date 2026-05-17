package ru.practicum.shareit.booking;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.State;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {
    @Autowired
    private final BookingService bookingService;

    @PostMapping
    public BookingDtoResponse addBooking(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                         @RequestBody BookingDtoRequest bookingDtoRequest) {
        return bookingService.addBooking(userId, bookingDtoRequest);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoResponse setApprove(@PathVariable Integer bookingId,
                                         @RequestHeader("X-Sharer-User-Id") Integer ownerId,
                                         @RequestParam("approved") Boolean isApproved) {
        return bookingService.setApprove(bookingId, ownerId, isApproved);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoResponse getBooking(@PathVariable Integer bookingId) {
        return bookingService.getBooking(bookingId);
    }

    @GetMapping
    public List<BookingDtoResponse> getAllBooking(@RequestParam(name = "state", defaultValue = "ALL") String searchState,
                                                  @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return bookingService.getAllBooking(State.valueOf(searchState), userId);
    }

    @GetMapping("/owner")
    public List<BookingDtoResponse> getAllByOwner(@RequestParam(name = "state", defaultValue = "ALL", required = false) String searchState,
                                                  @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return bookingService.getAllBookingByOwner(State.valueOf(searchState), userId);
    }
}