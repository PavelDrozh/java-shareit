package ru.practicum.shareit.item.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */

@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Entity
@EqualsAndHashCode
@Table(name = "items", schema = "public")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    User owner;

    @Column(name = "name", nullable = false)
    String name;

    @Column(name = "description", nullable = false)
    String description;

    @Column(name = "available", nullable = false)
    Boolean available;

    @OneToMany
    @JoinColumn(name = "comments")
    @ToString.Exclude
    List<Comment> comments;

    @ManyToOne
    @JoinColumn(name = "request_id")
    @ToString.Exclude
    ItemRequest request;
}
