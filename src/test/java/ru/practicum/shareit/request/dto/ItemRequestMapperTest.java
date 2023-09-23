package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemRequestMapperTest {

    private ItemRequest itemRequest;
    private ItemRequestDto itemRequestDto;
    private User user;

    @BeforeEach
    public void setUp() {
        user = mock(User.class);
        when(user.getName()).thenReturn("John Doe");
        itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("Please provide item X.");
        itemRequest.setRequestor(user);
        itemRequest.setCreated(LocalDateTime.now());

        itemRequestDto = new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getRequestor(),
                itemRequest.getCreated(),
                Collections.emptyList()
        );
    }

    @Test
    public void testToItemRequestDto() {
        ItemRequestDto mappedItemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);

        assertEquals(itemRequestDto.getId(), mappedItemRequestDto.getId());
        assertEquals(itemRequestDto.getDescription(), mappedItemRequestDto.getDescription());
        assertEquals(itemRequestDto.getRequester(), mappedItemRequestDto.getRequester());
        assertEquals(itemRequestDto.getCreated(), mappedItemRequestDto.getCreated());
    }

    @Test
    public void testToItemRequest() {
        ItemRequestDto testItemRequestDto = new ItemRequestDto(
                2L,
                "Please provide item Y.",
                user,
                LocalDateTime.now(),
                Collections.emptyList()
        );

        ItemRequest mappedItemRequest = ItemRequestMapper.toItemRequest(testItemRequestDto, user);

        assertEquals(testItemRequestDto.getId(), mappedItemRequest.getId());
        assertEquals(testItemRequestDto.getDescription(), mappedItemRequest.getDescription());
        assertEquals(user, mappedItemRequest.getRequestor());
        assertEquals(testItemRequestDto.getCreated(), mappedItemRequest.getCreated());
    }

    @Test
    public void testToItemRequestWithItemsDto() {
        ItemRequestDto mappedItemRequestDto = ItemRequestMapper.toItemRequestWithItemsDto(itemRequest);

        assertEquals(itemRequestDto.getId(), mappedItemRequestDto.getId());
        assertEquals(itemRequestDto.getDescription(), mappedItemRequestDto.getDescription());
        assertEquals(itemRequestDto.getRequester(), mappedItemRequestDto.getRequester());
        assertEquals(itemRequestDto.getCreated(), mappedItemRequestDto.getCreated());
        assertEquals(itemRequestDto.getItems(), mappedItemRequestDto.getItems());
    }
}
