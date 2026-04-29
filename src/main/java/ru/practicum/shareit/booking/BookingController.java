package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDtoResponse addBooking(@RequestHeader("X-Sharer-User-Id") @NotNull Integer userId,
                                         @Valid @RequestBody BookingDtoRequest bookingDtoRequest) {
        return bookingService.addBooking(userId, bookingDtoRequest);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoResponse setApprove(@PathVariable @NotNull Integer bookingId,
                                         @RequestHeader("X-Sharer-User-Id") @NotNull Integer ownerId,
                                         @RequestParam("approved") @NotNull Boolean isApproved) {
        return bookingService.setApprove(bookingId, ownerId, isApproved);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoResponse getBooking(@PathVariable @NotNull Integer bookingId,
                                         @RequestHeader("X-Sharer-User-Id") @NotNull Integer userId) {
        return bookingService.getBooking(bookingId, userId);
    }

    @GetMapping
    public List<BookingDtoResponse> getAllBooking(@RequestParam(name = "state", defaultValue = "ALL") String searchState,
                                                  @RequestHeader("X-Sharer-User-Id") @NotNull Integer userId) {
        return bookingService.getAllBooking(searchState, userId);
    }

    @GetMapping("/owner")
    public List<BookingDtoResponse> getAllByOwner(@RequestParam(name = "state", defaultValue = "ALL", required = false) String searchState,
                                                  @RequestHeader("X-Sharer-User-Id") @NotNull Integer userId) {
        return bookingService.getAllBookingByOwner(searchState, userId);
    }
}