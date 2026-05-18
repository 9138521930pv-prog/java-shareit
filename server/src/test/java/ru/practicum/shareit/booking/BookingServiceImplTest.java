package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringJUnitConfig
@SpringBootTest
public class BookingServiceImplTest {
    @MockBean
    private BookingRepository bookingRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private RequestRepository requestRepository;

    @MockBean
    private ItemRepository itemRepository;

    @Autowired
    private BookingServiceImpl bookingService;

    @Test
    void shouldAddBooking() {
        LocalDateTime startBooking = LocalDateTime.now().minusHours(1);
        LocalDateTime endBooking = LocalDateTime.now().plusHours(1);

        User booker = User.builder().id(1).name("booker").email("@booker.com").build();
        Integer bookerId = booker.getId();
        UserDto bookerDto = UserDto.builder().id(1).name("booker").email("@booker.com").build();

        User owner = User.builder().name("owner").email("@owner.com").build();

        ItemDto itemDto = ItemDto.builder().id(1).name("name1").ownerId(owner.getId())
                .available(true).description("description1").build();
        Item item = Item.builder().id(1).name("name1").owner(owner)
                .available(true).description("description1").build();

        Item itemNotAvailable = Item.builder().id(2).name("name1").owner(owner)
                .available(false).description("description1").build();

        Integer itemId = item.getId();

        Booking booking = Booking.builder().id(1).start(startBooking)
                .status(Status.WAITING).end(endBooking).item(item).booker(booker).build();

        BookingDtoRequest bookingDto = BookingDtoRequest.builder().id(1).start(startBooking)
                .status(Status.WAITING).end(endBooking).itemId(itemId).bookerId(bookerId).build();

        BookingDtoRequest bookingDtoWithUnAvailableItem = BookingDtoRequest.builder().id(1).start(startBooking)
                .status(Status.WAITING).end(endBooking).itemId(itemNotAvailable.getId()).bookerId(bookerId).build();

        Mockito.when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));
        Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        Mockito.when(bookingRepository.save(ArgumentMatchers.any())).thenReturn(booking);

        BookingDtoResponse savedBookingDto = bookingService.addBooking(bookerId, bookingDto);

        Assertions.assertNotNull(savedBookingDto);
        Assertions.assertEquals(savedBookingDto.getBooker().getName(), bookerDto.getName());
        Assertions.assertEquals(savedBookingDto.getItem().getName(), itemDto.getName());
        Assertions.assertEquals(savedBookingDto.getStatus(), Status.WAITING);

        Mockito.when(itemRepository.findById(itemNotAvailable.getId())).thenReturn(Optional.of(itemNotAvailable));

        assertThrows(ValidationException.class, () -> {
            bookingService.addBooking(bookerId, bookingDtoWithUnAvailableItem);
        });
    }

    @Test
    void shouldFindBookingById() {
        LocalDateTime startBooking = LocalDateTime.now().minusHours(1);
        LocalDateTime endBooking = LocalDateTime.now().plusHours(1);

        User booker = User.builder().id(1).name("booker").email("@booker.com").build();

        UserDto bookerDto = UserDto.builder().id(1).name("booker").email("@booker.com").build();

        User owner = User.builder().name("owner").email("@owner.com").build();

        ItemDto itemDto = ItemDto.builder().id(1).name("name1").ownerId(owner.getId())
                .available(true).description("description1").build();
        Item item = Item.builder().id(1).name("name1").owner(owner)
                .available(true).description("description1").build();

        Booking booking = Booking.builder().id(1).start(startBooking)
                .status(Status.WAITING).end(endBooking).item(item).booker(booker).build();

        Mockito.when(bookingRepository.findById(1)).thenReturn(Optional.ofNullable(booking));

        BookingDtoResponse savedBookingDto = bookingService.getBooking(1);

        Assertions.assertNotNull(savedBookingDto);
        Assertions.assertEquals(savedBookingDto.getBooker().getName(), bookerDto.getName());
        Assertions.assertEquals(savedBookingDto.getItem().getName(), itemDto.getName());
        Assertions.assertEquals(savedBookingDto.getStatus(), Status.WAITING);
    }

    @Test
    void shouldUpdateBookingStatus() {
        LocalDateTime startBooking = LocalDateTime.now().minusHours(2);
        LocalDateTime endBooking = LocalDateTime.now().minusHours(1);

        User booker = User.builder().id(1).name("booker").email("@booker.com").build();
        Integer bookerId = booker.getId();
        UserDto bookerDto = UserDto.builder().id(1).name("booker").email("@booker.com").build();

        User owner = User.builder().id(1).name("owner").email("@owner.com").build();

        ItemDto itemDto = ItemDto.builder().id(1).name("name1").ownerId(owner.getId())
                .available(true).description("description1").build();
        Item item = Item.builder().id(1).name("name1").owner(owner)
                .available(true).description("description1").build();

        Integer itemId = item.getId();

        Booking booking = Booking.builder().id(1).start(startBooking)
                .status(Status.WAITING).end(endBooking).item(item).booker(booker).build();

        Booking bookingWithRejectedStatus = Booking.builder().id(1).start(startBooking)
                .status(Status.REJECTED).end(endBooking).item(item).booker(booker).build();

        Booking bookingWithApprovedStatus = Booking.builder().id(1).start(startBooking)
                .status(Status.APPROVED).end(endBooking).item(item).booker(booker).build();

        Mockito.when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));
        Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        Mockito.when(bookingRepository.save(ArgumentMatchers.any())).thenReturn(bookingWithRejectedStatus);
        Mockito.when(bookingRepository.findById(1)).thenReturn(Optional.ofNullable(booking));

        BookingDtoResponse savedBookingDto = bookingService.setApprove(booking.getId(), owner.getId(), false);

        Assertions.assertNotNull(savedBookingDto);
        Assertions.assertEquals(savedBookingDto.getBooker().getName(), bookerDto.getName());
        Assertions.assertEquals(savedBookingDto.getItem().getName(), itemDto.getName());
        Assertions.assertEquals(savedBookingDto.getStatus(), Status.REJECTED);

        Mockito.when(bookingRepository.save(ArgumentMatchers.any())).thenReturn(bookingWithApprovedStatus);
        BookingDtoResponse savedBookingDtoApproved = bookingService
                .setApprove(booking.getId(), owner.getId(), false);
        Assertions.assertNotNull(savedBookingDtoApproved);
        Assertions.assertEquals(savedBookingDtoApproved.getStatus(), Status.APPROVED);

        assertThrows(ValidationException.class, () -> {
            bookingService.setApprove(booking.getId(), 3, false);
        });
    }

    @Test
    void shouldFindUsersBookings() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalDateTime startBooking = LocalDateTime.now().minusHours(2);
        LocalDateTime endBooking = LocalDateTime.now().minusHours(1);

        User booker = User.builder().id(1).name("booker").email("@booker.com").build();
        Integer bookerId = booker.getId();

        User owner = User.builder().name("owner").email("@owner.com").build();

        Item item = Item.builder().id(1).name("name1").owner(owner)
                .available(true).description("description1").build();

        Booking booking = Booking.builder().id(1).start(startBooking)
                .status(Status.WAITING).end(endBooking).item(item).booker(booker).build();
        List<Booking> bookingListWaiting = new ArrayList<>();
        bookingListWaiting.add(booking);

        Booking bookingPast = Booking.builder().id(1).start(startBooking)
                .status(Status.CANCELED).end(endBooking).item(item).booker(booker).build();
        List<Booking> bookingListPast = new ArrayList<>();
        bookingListPast.add(bookingPast);

        Booking bookingCurrent = Booking.builder().id(1).start(startBooking)
                .status(Status.APPROVED).end(endBooking).item(item).booker(booker).build();
        List<Booking> bookingListCurrent = new ArrayList<>();
        bookingListCurrent.add(bookingCurrent);

        Booking bookingFuture = Booking.builder().id(1).start(startBooking)
                .status(Status.APPROVED).end(endBooking).item(item).booker(booker).build();
        List<Booking> bookingListFuture = new ArrayList<>();
        bookingListFuture.add(bookingFuture);

        Booking bookingRejected = Booking.builder().id(1).start(startBooking)
                .status(Status.REJECTED).end(endBooking).item(item).booker(booker).build();
        List<Booking> bookingListRejected = new ArrayList<>();
        bookingListRejected.add(bookingRejected);

        List<Booking> bookingListAll = new ArrayList<>();
        bookingListAll.add(bookingRejected);
        bookingListAll.add(bookingCurrent);
        bookingListAll.add(bookingPast);
        bookingListAll.add(bookingFuture);
        bookingListAll.add(booking);
        Sort sort = Sort.by("start").descending();


/*
Sort sort = Sort.by("start").descending();
        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalDateTime currentTime = LocalDateTime.now();

        List<Booking> bookings = switch (bookingState) {
            case ALL -> bookingRepository.findByBooker_Id(userId, sort);
            case CURRENT -> bookingRepository.findByBooker_IdAndStartIsBeforeAndEndIsAfter(userId, currentDateTime, currentTime, sort);
            case PAST -> bookingRepository.findByBooker_IdAndEndIsBefore(userId, currentDateTime, sort);
            case FUTURE -> bookingRepository.findByBooker_IdAndStartIsAfter(userId, currentDateTime, sort);
            case WAITING -> bookingRepository.findByBooker_IdAndStatus(userId, Status.REJECTED, sort);
            case REJECTED -> bookingRepository.findByBooker_IdAndStatus(userId, Status.REJECTED, sort);
            default -> throw new NotFoundException("Неизвестный state: " + bookingState);
        };
* */
        Mockito.when(bookingRepository
                        .findByBooker_IdAndStatus(
                                ArgumentMatchers.eq(bookerId),
                                ArgumentMatchers.eq(Status.WAITING),
                                ArgumentMatchers.any(Sort.class)))
                .thenReturn(bookingListWaiting);
        Mockito.when(bookingRepository
                        .findByBooker_IdAndStatus(
                                ArgumentMatchers.eq(bookerId),
                                ArgumentMatchers.eq(Status.REJECTED),
                                ArgumentMatchers.any(Sort.class)))
                .thenReturn(bookingListRejected);
        Mockito.when(bookingRepository
                        .findByBooker_Id(bookerId, sort))
                .thenReturn(bookingListAll);
        Mockito.when(bookingRepository
                        .findByBooker_IdAndEndIsBefore(
                                ArgumentMatchers.eq(bookerId),
                                ArgumentMatchers.any(LocalDateTime.class),
                                ArgumentMatchers.any(Sort.class)))
                .thenReturn(bookingListPast);

        Mockito.when(bookingRepository
                        .findByBooker_IdAndStartIsAfter(
                                ArgumentMatchers.eq(bookerId),
                                ArgumentMatchers.any(LocalDateTime.class),
                                ArgumentMatchers.any(Sort.class)))
                .thenReturn(bookingListFuture);


        Mockito.when(bookingRepository
                        .findByBooker_IdAndStartIsBeforeAndEndIsAfter(
                                ArgumentMatchers.eq(bookerId),
                                ArgumentMatchers.any(LocalDateTime.class),
                                ArgumentMatchers.any(LocalDateTime.class),
                                ArgumentMatchers.any(Sort.class)))
                .thenReturn(bookingListCurrent);

        List<BookingDtoResponse> findBookingAllDtoList = bookingService.getAllBooking(State.ALL, bookerId);
        List<BookingDtoResponse> findBookingWaitingDtoList = bookingService.getAllBooking(State.WAITING, bookerId);
        List<BookingDtoResponse> findBookingRejectedDtoList = bookingService.getAllBooking(State.REJECTED, bookerId);
        List<BookingDtoResponse> findBookingPastDtoList = bookingService.getAllBooking(State.PAST, bookerId);
        List<BookingDtoResponse> findBookingCurrentDtoList = bookingService.getAllBooking(State.CURRENT, bookerId);
        List<BookingDtoResponse> findBookingFutureDtoList = bookingService.getAllBooking(State.FUTURE, bookerId);;

        Assertions.assertEquals(findBookingWaitingDtoList.get(0).getStatus(), Status.WAITING);
        Assertions.assertEquals(findBookingPastDtoList.get(0).getStatus(), Status.CANCELED);
        Assertions.assertEquals(findBookingCurrentDtoList.get(0).getStatus(), Status.APPROVED);
        Assertions.assertEquals(findBookingFutureDtoList.get(0).getStatus(), Status.APPROVED);
        Assertions.assertEquals(findBookingRejectedDtoList.get(0).getStatus(), Status.REJECTED);
        Assertions.assertEquals(findBookingAllDtoList.size(), 5);
    }

    @Test
    void shouldFindUsersItemsBookings() {
        LocalDateTime startBooking = LocalDateTime.now().minusHours(2);
        LocalDateTime endBooking = LocalDateTime.now().minusHours(1);

        User booker = User.builder().id(1).name("booker").email("@booker.com").build();

        User owner =User.builder().id(1).name("owner").email("@owner.com").build();
                //User.builder().id(1).name("owner").email("@owner.com").build();

        Integer ownerId = owner.getId();

        Item item = Item.builder().id(1).name("name1").owner(owner)
                .available(true).description("description1").build();
        Mockito.when(userRepository.findById(1))
                .thenReturn(Optional.of(owner));
        List<Item> itemList = new ArrayList<>();
        itemList.add(item);

        Booking booking = Booking.builder().id(1).start(startBooking)
                .status(Status.WAITING).end(endBooking).item(item).booker(booker).build();
        List<Booking> bookingListWaiting = new ArrayList<>();
        bookingListWaiting.add(booking);

        Booking bookingPast = Booking.builder().id(1).start(startBooking)
                .status(Status.CANCELED).end(endBooking).item(item).booker(booker).build();
        List<Booking> bookingListPast = new ArrayList<>();
        bookingListPast.add(bookingPast);

        Booking bookingCurrent = Booking.builder().id(1).start(startBooking)
                .status(Status.APPROVED).end(endBooking).item(item).booker(booker).build();
        List<Booking> bookingListCurrent = new ArrayList<>();
        bookingListCurrent.add(bookingCurrent);

        Booking bookingFuture = Booking.builder().id(1).start(startBooking)
                .status(Status.APPROVED).end(endBooking).item(item).booker(booker).build();
        List<Booking> bookingListFuture = new ArrayList<>();
        bookingListFuture.add(bookingFuture);

        Booking bookingRejected = Booking.builder().id(1).start(startBooking)
                .status(Status.REJECTED).end(endBooking).item(item).booker(booker).build();
        List<Booking> bookingListRejected = new ArrayList<>();
        bookingListRejected.add(bookingRejected);

        List<Booking> bookingListAll = new ArrayList<>();
        bookingListAll.add(bookingRejected);
        bookingListAll.add(bookingCurrent);
        bookingListAll.add(bookingPast);
        bookingListAll.add(bookingFuture);
        bookingListAll.add(booking);
        Mockito.when(itemRepository.findByOwnerId(ownerId)).thenReturn(itemList);
        Mockito.when(bookingRepository
                .findByItemOwnerIdAndStatus(
                        ArgumentMatchers.eq(item.getId()),
                        ArgumentMatchers.eq(Status.WAITING),
                        ArgumentMatchers.any(Sort.class)))
                .thenReturn(bookingListWaiting);
        Mockito.when(bookingRepository
                        .findByItemOwnerIdAndEndIsBefore(
                                ArgumentMatchers.eq(item.getId()),
                                ArgumentMatchers.any(LocalDateTime.class),
                                ArgumentMatchers.any(Sort.class)))
                .thenReturn(bookingListPast);
        Mockito.when(bookingRepository
                        .findByItemOwnerIdAndStartIsBeforeAndEndIsAfter(
                                ArgumentMatchers.eq(item.getId()),
                                ArgumentMatchers.any(LocalDateTime.class),
                                ArgumentMatchers.any(LocalDateTime.class),
                                ArgumentMatchers.any(Sort.class)))
                .thenReturn(bookingListCurrent);
        Mockito.when(bookingRepository
                        .findByItemOwnerIdAndStartIsAfter(
                                ArgumentMatchers.eq(item.getId()),
                                ArgumentMatchers.any(LocalDateTime.class),
                                ArgumentMatchers.any(Sort.class)))
                .thenReturn(bookingListFuture);
        Mockito.when(bookingRepository
                        .findByItemOwnerIdAndStatus(
                                ArgumentMatchers.eq(item.getId()),
                                ArgumentMatchers.eq(Status.REJECTED),
                                ArgumentMatchers.any(Sort.class)))
                .thenReturn(bookingListRejected);
        Mockito.when(bookingRepository
                        .findByItemOwnerId(ArgumentMatchers.eq(item.getId()), ArgumentMatchers.any(Sort.class)))
                .thenReturn(bookingListAll);

        List<BookingDtoResponse> findBookingWaitingDtoList = bookingService
                .getAllBookingByOwner(State.WAITING, ownerId);
        List<BookingDtoResponse> findBookingPastDtoList = bookingService
                .getAllBookingByOwner(State.PAST, ownerId);
        List<BookingDtoResponse> findBookingCurrentDtoList = bookingService
                .getAllBookingByOwner(State.CURRENT, ownerId);
        List<BookingDtoResponse> findBookingFutureDtoList = bookingService
                .getAllBookingByOwner(State.FUTURE, ownerId);
        List<BookingDtoResponse> findBookingRejectedDtoList = bookingService
                .getAllBookingByOwner(State.REJECTED, ownerId);
        List<BookingDtoResponse> findBookingAllDtoList = bookingService
                .getAllBookingByOwner(State.ALL, ownerId);

        Assertions.assertEquals(findBookingWaitingDtoList.get(0).getStatus(), Status.WAITING);
        Assertions.assertEquals(findBookingPastDtoList.get(0).getStatus(), Status.CANCELED);
        Assertions.assertEquals(findBookingCurrentDtoList.get(0).getStatus(), Status.APPROVED);
        Assertions.assertEquals(findBookingFutureDtoList.get(0).getStatus(), Status.APPROVED);
        Assertions.assertEquals(findBookingRejectedDtoList.get(0).getStatus(), Status.REJECTED);
        Assertions.assertEquals(findBookingAllDtoList.size(), 5);

        List<Item> emptyList = new ArrayList<>();
        Mockito.when(itemRepository.findByOwnerId(ownerId)).thenReturn(emptyList);

    }
}
