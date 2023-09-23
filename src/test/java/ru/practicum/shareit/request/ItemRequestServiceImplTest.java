package ru.practicum.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceImplTest {

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRequestRepository requestRepository;
    private Item itemOne;
    private Item itemTwo;
    private User userOne;
    private User userTwo;
    private ItemRequest itemRequest;
    private ItemRequestDto itemRequestDto;
    private final static String nowDate = "2023-08-24T01:29:22";
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @BeforeEach
    public void setUp() {


        userOne = new User(1, "name", "email");
        userTwo = new User(2, "name2", "email2");
        itemRequest = new ItemRequest(1L, "description1", userOne, LocalDateTime.parse(nowDate, formatter));
        itemOne = new Item(1, "item1", "description1", true, userTwo, itemRequest);
        itemTwo = new Item(2, "item2", "description2", true, userOne, itemRequest);
        itemRequestDto = new ItemRequestDto(1, "description1", userOne, LocalDateTime.parse(nowDate, formatter), null);

    }

    @Test
    public void testCreateRequest() throws NotFoundException, ValidationException {
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(userOne));
        when(requestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);

        ItemRequestDto createdRequest = itemRequestService.createRequest(itemRequestDto, 1L);

        assertThat(createdRequest).isNotNull();
        assertThat(createdRequest).isEqualTo(itemRequestDto);
    }

    @Test
    void testCreateRequestExpectExceptionValidationException() {
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(userOne));
        Assertions.assertThrows(ValidationException.class, () -> {
            itemRequestService.createRequest(new ItemRequestDto(), 1L);
        }, "Описание не заполнено!");
    }

    @Test
    void testCreateRequestExpectNotFoundUser() {
        Assertions.assertThrows(NotFoundException.class, () -> {
            itemRequestService.createRequest(new ItemRequestDto(), 1L);
        }, String.format("Пользователь %s не найден", 1L));
    }

    @Test
    public void testGetRequestsWithAnswers() throws NotFoundException {
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(userOne));
        when(requestRepository.findAllByRequestorId(any(Long.class))).thenReturn(Collections.singletonList(itemRequest));
        when(itemRepository.findAll()).thenReturn(Collections.singletonList(new Item()));

        List<ItemRequestDto> requests = itemRequestService.getRequestsWithAnswers(1L);

        assertThat(requests).isNotNull();
        assertThat(requests.get(0).getRequester()).isEqualTo(userOne);
    }

    @Test
    public void testGetRequestsWithAnswersExpectItemDtoIdNotNull() throws NotFoundException {
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(userOne));
        when(requestRepository.findAllByRequestorId(any(Long.class))).thenReturn(Collections.singletonList(itemRequest));
        when(itemRepository.findAll()).thenReturn(Collections.singletonList(itemOne));

        List<ItemRequestDto> requests = itemRequestService.getRequestsWithAnswers(1L);

        assertThat(requests).isNotNull();
        assertThat(requests.get(0).getRequester()).isEqualTo(userOne);
    }

    @Test
    void testGetRequestsWithAnswersExpectNotFoundUser() {
        Assertions.assertThrows(NotFoundException.class, () -> {
            itemRequestService.getRequestsWithAnswers(1L);
        }, String.format("Пользователь %s не найден", 1L));
    }

    @Test
    public void testGetAllRequests() {
        when(requestRepository.findAllByRequestorIdNot(any(Long.class), any())).thenReturn(new PageImpl<>(Collections.singletonList(itemRequest)));
        when(itemRepository.findAll()).thenReturn(Collections.singletonList(new Item()));

        List<ItemRequestDto> requests = itemRequestService.getAllRequests(1L, 0, 10);

        assertThat(requests).isNotNull();
        assertThat(requests.size()).isEqualTo(1);


    }

    @Test
    public void testGetAllRequestsExpectItemDtoIdNotNull() {
        when(requestRepository.findAllByRequestorIdNot(any(Long.class), any())).thenReturn(new PageImpl<>(Collections.singletonList(itemRequest)));
        when(itemRepository.findAll()).thenReturn(Collections.singletonList(itemOne));

        List<ItemRequestDto> requests = itemRequestService.getAllRequests(1L, 0, 10);

        assertThat(requests).isNotNull();
        assertThat(requests.size()).isEqualTo(1);

    }

    @Test
    public void testGetRequestById() throws NotFoundException {
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(userOne));
        when(requestRepository.findById(any(Long.class))).thenReturn(Optional.of(itemRequest));
        when(itemRepository.findAll()).thenReturn(Collections.singletonList(itemOne));

        ItemRequestDto request = itemRequestService.getRequestById(1L, 1L);

        assertThat(request).isNotNull();
        assertThat(request.getRequester()).isEqualTo(userOne);
    }

    @Test
    void testGetRequestByIdExpectNotFoundRequest() {
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(userOne));
        Assertions.assertThrows(NotFoundException.class, () -> {
            itemRequestService.getRequestById(1L, 1L);
        }, String.format("Запрос на вещь не найден!", 1L));
    }

    @Test
    void testGetRequestByIdExpectNotFoundUser() {
        Assertions.assertThrows(NotFoundException.class, () -> {
            itemRequestService.getRequestById(1L, 1L);
        }, String.format("Пользователь %s не найден", 1L));
    }
}