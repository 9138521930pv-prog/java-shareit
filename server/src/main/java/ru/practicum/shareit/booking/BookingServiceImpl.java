package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import jakarta.transaction.Transactional;
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
        User booker = userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Item item = itemRepository.findById(bookingDtoRequest.getItemId())
                .orElseThrow(() -> new NotFoundException("Item not found"));
        if (!item.getAvailable()) {
            throw new ValidationException("Предмет недоступен для брони.");
        }
        bookingDtoRequest.setStatus(Status.WAITING);

        Booking booking = bookingRepository.save(bookingMapper.mapToBooking(bookingDtoRequest, booker, item));

  //      log.info("Пользователь с id {}, создал бронь с id {} на вещь с id {}", bookerId, booking.getId(), item.getId());
        return bookingMapper.mapToDtoResponse(booking);
    }

    @Override
    @Transactional
    public BookingDtoResponse setApprove(Integer bookingId, Integer userId, Boolean isApproved) {
  //      public BookingDtoResponse     (Integer requestOwnerId, Integer bookingId, boolean status) {
            Booking booking = bookingRepository.findById(bookingId)
                    .orElseThrow(() -> new NotFoundException("Booking not found"));
            Integer ownerId = booking.getItem().getOwner().getId();

            if (!Objects.equals(userId, ownerId)) {
//                log.error("id владельца вещи {} не совпадает с id {} запроса.", ownerId, userId);
                throw new ValidationException("Изменять статус брони может только владелец вещи, " +
                        "id владельца вещи не совпадает с id запрашивающего изменение пользователя");
            }

            if (isApproved) {
                booking.setStatus(Status.APPROVED);
            } else {
                booking.setStatus(Status.REJECTED);
            }
//            log.info("Обновлен статус бронирования с id {}", bookingId);
            return bookingMapper.mapToDtoResponse(bookingRepository.save(booking));

    }

    @Override
    public BookingDtoResponse getBooking(Integer bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));

        //log.info("Найдена бронь по id {}", bookingId);
        return bookingMapper.mapToDtoResponse(booking);
        }


    public List<BookingDtoResponse> getAllBooking(State bookingState, Integer userId) {
        Sort sort = Sort.by("start").descending();
        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalDateTime currentTime = LocalDateTime.now();

        List<Booking> bookings = switch (bookingState) {
            case ALL -> bookingRepository.findByBooker_Id(userId, sort);
            case CURRENT -> bookingRepository.findByBooker_IdAndStartIsBeforeAndEndIsAfter(userId, currentDateTime, currentTime, sort);
            case PAST -> bookingRepository.findByBooker_IdAndEndIsBefore(userId, currentDateTime, sort);
            case FUTURE -> bookingRepository.findByBooker_IdAndStartIsAfter(userId, currentDateTime, sort);
            case WAITING -> bookingRepository.findByBooker_IdAndStatus(userId, Status.WAITING, sort);
            case REJECTED -> bookingRepository.findByBooker_IdAndStatus(userId, Status.REJECTED, sort);
            default -> throw new NotFoundException("Неизвестный state: " + bookingState);
        };

        return bookingMapper.mapToDtoResponse(bookings);
        }

    public List<BookingDtoResponse> getAllBookingByOwner(State bookingState, Integer userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new NotFoundException("User with id = " + userId + " was not found.");
        }

        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalDateTime dateTime = LocalDateTime.now();
        Sort sort = Sort.by("start").descending();

        List<Booking> bookings = switch (bookingState) {
            case ALL -> bookingRepository.findByItemOwnerId(userId, sort);
            case CURRENT -> bookingRepository.findByItemOwnerIdAndStartIsBeforeAndEndIsAfter(userId, currentDateTime, dateTime, sort);
            case PAST -> bookingRepository.findByItemOwnerIdAndEndIsBefore(userId, currentDateTime, sort);
            case FUTURE -> bookingRepository.findByItemOwnerIdAndStartIsAfter(userId, currentDateTime, sort);
            case WAITING -> bookingRepository.findByItemOwnerIdAndStatus(userId, Status.WAITING, sort);
            case REJECTED -> bookingRepository.findByItemOwnerIdAndStatus(userId, Status.REJECTED, sort);
            default -> throw new NotFoundException("Неизвестный state");
            };
         return bookingMapper.mapToDtoResponse(bookings);
       }
}

