package ru.practicum.shareit.user.repository;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class InMemoryUserRepository implements UserRepository {

    final Map<Long, User> usersMap = new HashMap<>();
    long currentId;

    @Override
    public List<User> getAll() {
        List<User> users = new ArrayList<>(usersMap.values());
        log.info("Текущее количество пльзователей: {}", users.size());
        return users;
    }

    @Override
    public Optional<User> getById(long id) {
        Optional<User> user = Optional.ofNullable(usersMap.get(id));
        log.info("Пользователь с id {} найден - {}", id, user.isPresent());
        return user;
    }

    @Override
    public Optional<User> getByEmail(String email) {
        Optional<User> user = usersMap.values().stream().filter(u -> u.getEmail().equalsIgnoreCase(email)).findFirst();
        log.info("Пользователь с email {} найден - {}", email, user.isPresent());
        return user;
    }

    @Override
    public User create(User user) {
        User newUser = User.builder()
                .email(user.getEmail())
                .name(user.getName())
                .id(++currentId)
                .build();
        usersMap.put(newUser.getId(), newUser);
        log.info("Пользователь создан - {}", newUser);
        return newUser;
    }

    @Override
    public User update(User user) {
        User userForUpdate = usersMap.get(user.getId());
        if (user.getName() != null && !user.getName().isBlank()) {
            userForUpdate.setName(user.getName());
        }
        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            userForUpdate.setEmail(user.getEmail());
        }
        usersMap.put(userForUpdate.getId(), userForUpdate);
        log.info("Пользователь обновлен - {}", userForUpdate);
        return userForUpdate;
    }

    @Override
    public Optional<User> deleteById(long id) {
        Optional<User> deleted = Optional.ofNullable(usersMap.remove(id));
        log.info("Пользователь удален - {}", deleted);
        return deleted;
    }
}
