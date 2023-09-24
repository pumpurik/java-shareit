package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.Map;

@Service
public class RequestClient extends BaseClient {

    @Autowired
    public RequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(String.format("%s/requests", serverUrl)))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> getRequests(long userId, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("/all?from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> getRequestsWithAnswers(long userId) {

        return get("", userId);
    }


    public ResponseEntity<Object> create(long userId, ItemRequestDto itemRequestDto) {
        return post("", userId, itemRequestDto);
    }

    public ResponseEntity<Object> getRequest(long userId, Long requestId) {
        return get(String.format("/%s", requestId), userId);
    }
}
