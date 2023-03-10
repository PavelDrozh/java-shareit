package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.BookStatus;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.Collection;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Page<Booking> findAllByBookerIdAndStatusInOrderByStartDesc(Long bookerId, Collection<BookStatus> status,
                                                               Pageable pageable);

    Page<Booking> findAllByBookerIdAndStatusInAndEndIsBeforeOrderByStartDesc(Long bookerId,
                                                                             Collection<BookStatus> status,
                                                                             LocalDateTime end,
                                                                             Pageable pageable);

    Page<Booking> findAllByBookerIdAndStatusInAndStartIsAfterOrderByStartDesc(Long bookerId,
                                                                              Collection<BookStatus> status,
                                                                              LocalDateTime start,
                                                                              Pageable pageable);

    Page<Booking> findAllByBookerIdAndStatusInAndStartIsBeforeAndEndIsAfterOrderByStartDesc(Long bookerId,
                                                                                          Collection<BookStatus> status,
                                                                                            LocalDateTime start,
                                                                                            LocalDateTime end,
                                                                                            Pageable pageable);

    Page<Booking> findAllByItem_OwnerAndStatusInOrderByStartDesc(Long itemOwner, Collection<BookStatus> status,
                                                                 Pageable pageable);

    Page<Booking> findAllByItem_OwnerAndStatusInAndEndIsBeforeOrderByStartDesc(Long bookerId,
                                                                               Collection<BookStatus> status,
                                                                               LocalDateTime end,
                                                                               Pageable pageable);

    Page<Booking> findAllByItem_OwnerAndStatusInAndStartIsAfterOrderByStartDesc(Long bookerId,
                                                                                Collection<BookStatus> status,
                                                                                LocalDateTime start,
                                                                                Pageable pageable);

    Page<Booking> findAllByItem_OwnerAndStatusInAndStartIsBeforeAndEndIsAfterOrderByStartDesc(Long bookerId,
                                                                                         Collection<BookStatus> status,
                                                                                              LocalDateTime start,
                                                                                              LocalDateTime end,
                                                                                              Pageable pageable);

    Booking findFirstByItemIdAndEndBeforeOrderByEndDesc(Long itemId, LocalDateTime end);

    Booking findFirstByItemIdAndStartAfterOrderByStart(Long itemId, LocalDateTime start);

}
