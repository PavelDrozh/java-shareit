package ru.practicum.shareit.booking.model;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.GenerationType;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import javax.persistence.Column;
import javax.persistence.Enumerated;
import javax.persistence.EnumType;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "booking", schema = "public")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "item_id")
    @ToString.Exclude
    Item item;

    @ManyToOne
    @JoinColumn(name = "booker_id")
    @ToString.Exclude
    User booker;

    @Column(name = "start_booking")
    LocalDateTime start;

    @Column(name = "end_booking")
    LocalDateTime end;

    @Enumerated(EnumType.STRING)
    BookStatus status;
}
