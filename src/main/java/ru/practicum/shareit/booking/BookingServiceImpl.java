package ru.practicum.shareit.booking;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.model.State.ALL;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public BookingDtoResponse addBooking(Integer bookerId, BookingDtoRequest bookingDtoRequest) {
        validateBookingDtoInput(bookingDtoRequest);
        validateUser(bookerId);
        validateItem(bookingDtoRequest.getItemId());

        Optional<Item> optionalItem = itemRepository.findById(bookingDtoRequest.getItemId());
        if (optionalItem.isEmpty()) {
            throw new NotFoundException("Failed to receive item.");
        }
        Item item = optionalItem.get();

        if (!item.getAvailable()) {
            throw new UnavailableItemBookingException("Failed to create booking. Unavailable items can't be booked.");
        }
        if (item.getOwner().getId().equals(bookerId)) {
            throw new IllegalItemBookingException("Failed to create booking. Item owners are not allowed to book their own items.");
        }

        Booking booking = bookingMapper.mapToBooking(bookingDtoRequest);
        booking.setBooker(userRepository.findById(bookerId).get());
        booking.setStatus(Status.WAITING);
        booking.setItem(item);

        booking = bookingRepository.save(booking);
        return bookingMapper.mapToBookingDto(booking);
    }

    @Override
    @Transactional
    public BookingDtoResponse setApprove(Integer bookingId, Integer userId, Boolean isApproved) {
        validateBooking(bookingId);

        Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);
        if (optionalBooking.isEmpty()) {
            throw new NotFoundException("Booking with id = " + bookingId + " was not found.");
        }
        Booking booking = optionalBooking.get();

        Integer itemOwnerId = booking.getItem().getOwner().getId();

        if (!Objects.equals(userId, itemOwnerId)) {
            throw new IllegalItemBookingException("Failed to change booking status. Only item owners are allowed to change booking status.");
        }

        if (!booking.getStatus().equals(Status.WAITING)) {
            throw new UnavailableItemBookingException("Booking status must be 'WAITING'.");
        }

        if (isApproved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }

        booking = bookingRepository.save(booking);
        return bookingMapper.mapToBookingDto(booking);
    }

    @Override
    public BookingDtoResponse getBooking(Integer bookingId, Integer userId) {
        validateBooking(bookingId);
        validateUser(userId);

        Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);
        if (optionalBooking.isEmpty()) {
            throw new NotFoundException("Booking with id = " + bookingId + "was not found.");
        }
        Booking booking = optionalBooking.get();

        Integer itemOwnerId = booking.getItem().getOwner().getId();
        Integer bookerId = booking.getBooker().getId();

        if (!userId.equals(itemOwnerId) && !userId.equals(bookerId)) {
            throw new NotFoundException("Failed to get booking. Only item owners and item bookers are allowed to view bookings.");
        }

        return bookingMapper.mapToBookingDto(booking);
    }

    public List<BookingDtoResponse> getAllBooking(String bookingState, Integer userId) {
        validateUser(userId);
        Sort sort = Sort.by("start").descending();

        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new NotFoundException("User with id = " + userId + "was not found.");
        }

        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalDateTime currentTime = LocalDateTime.now();
        State searchMode;

        try {
            searchMode = State.valueOf(bookingState.toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new IllegalSearchModeException("Unknown state: " + bookingState);
        }
        switch (searchMode) {
            case ALL:
                return bookingRepository.findByBooker_Id(userId, sort).stream()
                        .map(bookingMapper::mapToBookingDto)
                        .collect(Collectors.toList());
            case CURRENT:
                return bookingRepository.findByBooker_IdAndStartIsBeforeAndEndIsAfter(userId, currentDateTime, currentTime, sort).stream()
                        .map(bookingMapper::mapToBookingDto)
                        .collect(Collectors.toList());
            case PAST:
                return bookingRepository.findByBooker_IdAndEndIsBefore(userId, currentDateTime, sort).stream()
                        .map(bookingMapper::mapToBookingDto)
                        .collect(Collectors.toList());
            case FUTURE:
                return bookingRepository.findByBooker_IdAndStartIsAfter(userId, currentDateTime, sort).stream()
                        .map(bookingMapper::mapToBookingDto)
                        .collect(Collectors.toList());
            case WAITING:
                return bookingRepository.findByBooker_IdAndStatus(userId, Status.WAITING, sort).stream()
                        .map(bookingMapper::mapToBookingDto)
                        .collect(Collectors.toList());
            case REJECTED:
                return bookingRepository.findByBooker_IdAndStatus(userId, Status.REJECTED, sort).stream()
                        .map(bookingMapper::mapToBookingDto)
                        .collect(Collectors.toList());
            default:
                throw new IllegalSearchModeException("Unknown state: " + bookingState);
        }
    }

    public List<BookingDtoResponse> getAllBookingByOwner(String bookingState, Integer userId) {
        validateUser(userId);
        Sort sort = Sort.by("start").descending();

        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new NotFoundException("User with id = " + userId + " was not found.");
        }

        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalDateTime dateTime = LocalDateTime.now();

        switch (bookingState) {
            case "ALL":
                return bookingRepository.findByItemOwnerId(userId, sort).stream()
                        .map(bookingMapper::mapToBookingDto)
                        .collect(Collectors.toList());
            case "CURRENT":
                return bookingRepository.findByItemOwnerIdAndStartIsBeforeAndEndIsAfter(userId, currentDateTime, dateTime, sort).stream()
                        .map(bookingMapper::mapToBookingDto)
                        .collect(Collectors.toList());
            case "PAST":
                return bookingRepository.findByItemOwnerIdAndEndIsBefore(userId, currentDateTime, sort).stream()
                        .map(bookingMapper::mapToBookingDto)
                        .collect(Collectors.toList());
            case "FUTURE":
                return bookingRepository.findByItemOwnerIdAndStartIsAfter(userId, currentDateTime, sort).stream()
                        .map(bookingMapper::mapToBookingDto)
                        .collect(Collectors.toList());
            case "WAITING":
                return bookingRepository.findByItemOwnerIdAndStatus(userId, Status.WAITING, sort).stream()
                        .map(bookingMapper::mapToBookingDto)
                        .collect(Collectors.toList());
            case "REJECTED":
                return bookingRepository.findByItemOwnerIdAndStatus(userId, Status.REJECTED, sort).stream()
                        .map(bookingMapper::mapToBookingDto)
                        .collect(Collectors.toList());
            default:
                throw new IllegalSearchModeException("Unknown state: " + bookingState);
        }
    }

    private void validateBookingDtoInput(BookingDtoRequest bookingDtoRequest) {
        if (bookingDtoRequest.getItemId() == null
                || bookingDtoRequest.getStart() == null || bookingDtoRequest.getEnd() == null
                || bookingDtoRequest.getStart().isAfter(bookingDtoRequest.getEnd())
                || bookingDtoRequest.getEnd().isBefore(bookingDtoRequest.getStart())
                || bookingDtoRequest.getStart().equals(bookingDtoRequest.getEnd())
        ) {
            throw new ValidationException("Failed to process request. " +
                    "Booker id, item id, start and end time must not be empty. " +
                    "Start and end time must be correct.");
        }
    }

    private void validateUser(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Failed to process request. User with id = " + userId + " doesn't exist.!");
        }
    }

    private void validateItem(Integer itemId) {
        if (!itemRepository.existsById(itemId)) {
            throw new NotFoundException("Failed to process request. Item with id = " + itemId + " doesn't exist.");
        }
    }

    private void validateBooking(Integer bookingId) {
        if (!bookingRepository.existsById(bookingId)) {
            throw new NotFoundException("Failed to process request. Booking with id = " + bookingId + " doesn't exist.");
        }
    }
}