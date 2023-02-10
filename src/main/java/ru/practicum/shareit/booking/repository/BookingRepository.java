package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.BookStatus;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerIdAndStatusInOrderByStartDesc(Long bookerId, Collection<BookStatus> status);

    List<Booking> findAllByBookerIdAndStatusInAndEndIsBeforeOrderByStartDesc(Long bookerId,
                                                                             Collection<BookStatus> status,
                                                                             LocalDateTime end);

    List<Booking> findAllByBookerIdAndStatusInAndStartIsAfterOrderByStartDesc(Long bookerId,
                                                                              Collection<BookStatus> status,
                                                                              LocalDateTime start);

    List<Booking> findAllByBookerIdAndStatusInAndStartIsBeforeAndEndIsAfterOrderByStartDesc(Long bookerId,
                                                                                          Collection<BookStatus> status,
                                                                                            LocalDateTime start,
                                                                                            LocalDateTime end);

    List<Booking> findAllByItem_OwnerAndStatusInOrderByStartDesc(Long itemOwner, Collection<BookStatus> status);

    List<Booking> findAllByItem_OwnerAndStatusInAndEndIsBeforeOrderByStartDesc(Long bookerId,
                                                                               Collection<BookStatus> status,
                                                                               LocalDateTime end);

    List<Booking> findAllByItem_OwnerAndStatusInAndStartIsAfterOrderByStartDesc(Long bookerId,
                                                                                Collection<BookStatus> status,
                                                                                LocalDateTime start);

    List<Booking> findAllByItem_OwnerAndStatusInAndStartIsBeforeAndEndIsAfterOrderByStartDesc(Long bookerId,
                                                                                         Collection<BookStatus> status,
                                                                                              LocalDateTime start,
                                                                                              LocalDateTime end);

    Booking findFirstByItemIdAndEndBeforeOrderByEndDesc(Long itemId, LocalDateTime end);

    Booking findFirstByItemIdAndStartAfterOrderByStart(Long itemId, LocalDateTime start);

}
