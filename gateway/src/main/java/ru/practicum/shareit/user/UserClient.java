package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.user.dto.UserCreatorDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.util.Map;

@Service
public class UserClient extends BaseClient {

    private static final String API_PREFIX = "/users";

    public UserClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> getAll(int from, int size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("?from={from}&size={size}", parameters);
    }

    public ResponseEntity<Object> createUser(UserCreatorDto dto) {
        return post("", dto);
    }

    public ResponseEntity<Object> updateUser(UserUpdateDto dto, long id) {
        return patch("/" + id, dto);
    }

    public ResponseEntity<Object> getById(long id) {
        return get("/" + id);
    }

    public ResponseEntity<Object> deleteUser(long id) {
        return delete("/" + id);
    }
}
