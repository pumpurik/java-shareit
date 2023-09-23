package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingTwoFieldsDto;
import ru.practicum.shareit.booking.dto.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {
    @InjectMocks
    private ItemServiceImpl itemService;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    private Booking bookingOne;
    private Booking bookingTwo;
    private Item itemOne;
    private Item itemTwo;
    private User userOne;
    private User userTwo;
    private ItemRequest itemRequest;
    private ItemDto itemDto;
    private Comment comment;
    private ItemDtoWithBooking itemDtoWithBooking;
    private BookingTwoFieldsDto nextBookingTwoFieldsDto;
    private BookingTwoFieldsDto lastBookingTwoFieldsDto;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private static final String startTimeOne = "2023-06-21T01:29:22";
    private static final String endTimeOne = "2023-10-22T02:30:22";
    private static final String nowDate = "2023-08-24T01:29:22";
    private static final String startTimeTwo = "2023-08-21T01:29:22";
    private static final String endTimeTwo = "2023-08-30T02:30:22";

    @BeforeEach
    public void setUp() {
        itemRequest = new ItemRequest();

        userOne = new User(1, "name", "email");
        userTwo = new User(2, "name2", "email2");
        itemOne = new Item(1, "item1", "description1", true, userTwo, itemRequest);
        itemTwo = new Item(2, "item2", "description2", true, userOne, itemRequest);
        comment = new Comment(1, "text", itemOne, userOne, LocalDateTime.parse(nowDate, formatter));
        bookingOne = new Booking(1, LocalDateTime.parse(startTimeOne, formatter), LocalDateTime.parse(endTimeOne, formatter), itemTwo, userOne, Status.WAITING);
        bookingTwo = new Booking(2, LocalDateTime.parse(startTimeTwo, formatter), LocalDateTime.parse(endTimeTwo, formatter), itemOne, userTwo, Status.APPROVED);
        nextBookingTwoFieldsDto = new BookingTwoFieldsDto(1L, 1L);
        lastBookingTwoFieldsDto = new BookingTwoFieldsDto(1L, 1L);
        itemDtoWithBooking = new ItemDtoWithBooking(1, "item1", "description1", true, lastBookingTwoFieldsDto, nextBookingTwoFieldsDto, Collections.emptyList());
        itemDto = new ItemDto(1, "item1", "description1", true, userOne, 1L);
    }

    @Test
    public void testCreateItem() throws NotFoundException, ValidationException {
        User user = new User();
        user.setId(1L);
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Test Item");
        itemDto.setDescription("Test Description");
        itemDto.setAvailable(true);
        itemDto.setRequestId(2L);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(2L);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(itemDto.getRequestId())).thenReturn(Optional.of(itemRequest));
        when(itemRepository.save(any(Item.class))).thenReturn(itemOne);

        ItemDto result = itemService.createItem(itemDto, user.getId());

        assertThat(result).isNotNull();
        assertThat(result.getAvailable()).isEqualTo(true);
        assertThat(result.getName()).isEqualTo(itemOne.getName());
        assertThat(result.getDescription()).isEqualTo(itemOne.getDescription());
    }

    @Test
    public void testCreateItemExpectNotValidateItemFromUser() {
        Assertions.assertThrows(ValidationException.class, () -> {
            itemService.createItem(new ItemDto(), 1L);
        }, "Для добавления вещи необходимо заполнить следующие поля: имя вещи, " +
                "доступность вещи и описание вещи");
    }

    @Test
    public void testCreateItemExpectNotFoundExceptionUser() {
        Assertions.assertThrows(NotFoundException.class, () -> {
            itemService.createItem(itemDto, 1L);
        }, String.format("Пользователь %s не найден", 1L));
    }

    @Test
    public void testUpdateItem() throws NotFoundException {
        User user = new User();
        user.setId(1L);
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Updated Item");
        itemDto.setDescription("Updated Description");
        itemDto.setAvailable(true);
        when(itemRepository.save(any())).thenReturn(itemOne);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(itemOne));
        when(itemRepository.findByIdAndOwnerId(any(), any())).thenReturn(Optional.of(itemOne));

        ItemDto result = itemService.updateItem(itemDto, 1L, user.getId());

        assertThat(result).isNotNull();
        assertThat(result.getAvailable()).isEqualTo(true);
        assertThat(result.getName()).isEqualTo(itemOne.getName());
        assertThat(result.getDescription()).isEqualTo(itemOne.getDescription());
    }

    @Test
    public void testUpdateItemExpectNotValidateOwnerOfItem() {
        when(itemRepository.findByIdAndOwnerId(anyLong(), anyLong())).thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class, () -> {
            itemService.updateItem(itemDto, 1L, 1L);
        }, String.format("Пользователь c идентификатором %s не является владельцем вещи", 1L));
    }

    @Test
    public void testGetItemById() throws NotFoundException {
        long itemId = 1L;
        long userId = 2L;

        when(itemRepository.findByIdAndOwnerId(itemId, userId)).thenReturn(Optional.of(itemOne));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(itemOne));
        when(commentRepository.findAllCommentByItemId(itemId)).thenReturn(List.of(comment));

        ItemDtoWithBooking result = itemService.getItemById(itemId, userId);

        assertThat(result).isNotNull();
        assertThat(result.getAvailable()).isEqualTo(true);
        assertThat(result.getName()).isEqualTo(itemOne.getName());
        assertThat(result.getDescription()).isEqualTo(itemOne.getDescription());
    }

    @Test
    public void testGetItemByIdLastPresentAndStatusNotREJECTED() throws NotFoundException {
        long itemId = 1L;
        long userId = 2L;


        when(itemRepository.findByIdAndOwnerId(itemId, userId)).thenReturn(Optional.of(itemOne));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(itemOne));
        when(bookingRepository.findFirstByItemIdAndStartLessThanEqualOrderByStartDesc(anyLong(), any())).thenReturn(Optional.of(bookingOne));
        when(bookingRepository.findFirstByItemIdAndStartAfterOrderByStartAsc(anyLong(), any())).thenReturn(Optional.of(bookingTwo));
        when(commentRepository.findAllCommentByItemId(itemId)).thenReturn(List.of(comment));

        ItemDtoWithBooking result = itemService.getItemById(itemId, userId);

        assertThat(result).isNotNull();
        assertThat(result.getAvailable()).isEqualTo(true);
        assertThat(result.getName()).isEqualTo(itemOne.getName());
        assertThat(result.getDescription()).isEqualTo(itemOne.getDescription());
    }

    @Test
    public void testGetItemByIdNotFindByIdAndOwnerId() {
        long itemId = 1L;
        long userId = 2L;

        when(itemRepository.findByIdAndOwnerId(anyLong(), anyLong())).thenReturn(Optional.of(itemOne));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());
        when(commentRepository.findAllCommentByItemId(anyLong())).thenReturn(List.of(comment));
        Assertions.assertThrows(NotFoundException.class, () -> {
            itemService.getItemById(itemId, userId);
        }, "Вещь не найдена");
    }

    @Test
    public void testGetItemByIdElseFindByIdAndOwnerIdPresentComment() throws NotFoundException {
        long itemId = 1L;
        long userId = 2L;

        when(itemRepository.findByIdAndOwnerId(anyLong(), anyLong())).thenReturn(Optional.empty());
        when(commentRepository.findAllCommentByItemId(anyLong())).thenReturn(List.of(comment));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(itemOne));

        ItemDtoWithBooking result = itemService.getItemById(itemId, userId);
        assertThat(result).isNotNull();
        assertThat(result.getAvailable()).isEqualTo(true);
        assertThat(result.getName()).isEqualTo(itemOne.getName());
        assertThat(result.getDescription()).isEqualTo(itemOne.getDescription());

    }

    @Test
    public void testGetItemByIdElseFindByIdAndOwnerIdNotPresentComment() throws NotFoundException {
        long itemId = 1L;
        long userId = 2L;

        when(itemRepository.findByIdAndOwnerId(anyLong(), anyLong())).thenReturn(Optional.empty());
        when(commentRepository.findAllCommentByItemId(anyLong())).thenReturn(Collections.emptyList());
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> {
            itemService.getItemById(itemId, userId);
        }, "Вещь не найдена");
    }

    @Test
    public void testGetItemByIdElseFindByIdAndOwnerIdNotFindItem() throws NotFoundException {
        long itemId = 1L;
        long userId = 2L;

        when(itemRepository.findByIdAndOwnerId(anyLong(), anyLong())).thenReturn(Optional.empty());
        when(commentRepository.findAllCommentByItemId(anyLong())).thenReturn(Collections.emptyList());
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(itemOne));

        ItemDtoWithBooking result = itemService.getItemById(itemId, userId);
        assertThat(result).isNotNull();
        assertThat(result.getAvailable()).isEqualTo(true);
        assertThat(result.getName()).isEqualTo(itemOne.getName());
        assertThat(result.getDescription()).isEqualTo(itemOne.getDescription());

    }

    @Test
    public void testGetAllItemsForOwnerPageable() {
        long userId = 1L;
        Pageable pageable = Pageable.unpaged();

        when(itemRepository.findAllByOwnerId(anyLong(), any())).thenReturn(List.of(itemOne, itemTwo));
        when(bookingRepository.findFirstByItemIdAndStartLessThanEqualOrderByStartDesc(anyLong(), any())).thenReturn(Optional.of(bookingOne));
        when(bookingRepository.findFirstByItemIdAndStartAfterOrderByStartAsc(anyLong(), any())).thenReturn(Optional.of(bookingOne));
        List<ItemDtoWithBooking> result = itemService.getAllItemsForOwner(userId, pageable);
        assertThat(result).isNotNull();
        assertThat(result).contains(itemDtoWithBooking);
    }

    @Test
    public void testGetAllItemsForOwnerPageableNotEmptyCommentLatNotPresentAndNextNotPresent() {
        long userId = 1L;
        Pageable pageable = Pageable.unpaged();
        when(commentRepository.findAllCommentByItemId(anyLong())).thenReturn(List.of(comment));
        when(itemRepository.findAllByOwnerId(anyLong(), any())).thenReturn(List.of(itemOne, itemTwo));
        when(bookingRepository.findFirstByItemIdAndStartLessThanEqualOrderByStartDesc(anyLong(), any())).thenReturn(Optional.empty());
        when(bookingRepository.findFirstByItemIdAndStartAfterOrderByStartAsc(anyLong(), any())).thenReturn(Optional.empty());
        List<ItemDtoWithBooking> result = itemService.getAllItemsForOwner(userId, pageable);
        assertThat(result).isNotNull();
    }

    @Test
    public void testGetAllItemsForOwner() {
        long userId = 1L;

        when(itemRepository.findAllByOwnerId(anyLong())).thenReturn(List.of(itemOne, itemTwo));
        when(bookingRepository.findFirstByItemIdAndStartLessThanEqualOrderByStartDesc(anyLong(), any())).thenReturn(Optional.of(bookingOne));
        when(bookingRepository.findFirstByItemIdAndStartAfterOrderByStartAsc(anyLong(), any())).thenReturn(Optional.of(bookingOne));
        List<ItemDtoWithBooking> result = itemService.getAllItemsForOwner(userId, null);
        assertThat(result).isNotNull();
        assertThat(result).contains(itemDtoWithBooking);
    }

    @Test
    public void testGetSearchItems() {
        when(itemRepository.search(anyString())).thenReturn(List.of(itemOne, itemTwo));
        List<ItemDto> text = itemService.getSearchItems("text", null);
        assertThat(text).isNotNull();
    }


    @Test
    public void testGetSearchItemsPageable() {
        Pageable pageable = Pageable.unpaged();
        when(itemRepository.findAll(anyString(), any())).thenReturn(List.of(itemOne, itemTwo));
        List<ItemDto> text = itemService.getSearchItems("text", pageable);
        assertThat(text).isNotNull();
    }

    @Test
    public void testCreateComment() throws ValidationException, NotFoundException {
        long itemId = 1L;
        long userId = 2L;
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Test Comment");

        User user = new User();
        user.setId(userId);

        Item item = new Item();
        item.setId(itemId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.findFirstByBookerIdAndItemIdAndEndLessThanEqual(any(), any(), any()))
                .thenReturn(Optional.of(bookingOne));
        when(commentRepository.save(any())).thenReturn(comment);

        CommentDto result = itemService.createComment(commentDto, itemId, userId);

        assertThat(result).isNotNull();
        assertThat(result.getText()).isEqualTo(comment.getText());
        assertThat(result.getAuthorName()).isEqualTo(comment.getAuthor().getName());
    }

    @Test
    public void testCreateCommentExpectValidationException() {
        long itemId = 1L;
        long userId = 2L;
        User user = new User();
        user.setId(userId);

        Item item = new Item();
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Test Comment");
        item.setId(itemId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.findFirstByBookerIdAndItemIdAndEndLessThanEqual(any(), any(), any()))
                .thenReturn(Optional.empty());
        Assertions.assertThrows(ValidationException.class, () -> {
            itemService.createComment(commentDto, itemId, userId);
        }, "Ошибка ввода данных!");
    }

    @Test
    public void testCreateCommentExpectNotFoundExceptionUser() {
        Assertions.assertThrows(NotFoundException.class, () -> {
            itemService.createComment(new CommentDto(), 1L, 1L);
        }, String.format("Пользователь %s не найден", 1L));
    }

    @Test
    public void testCreateCommentExpectNotFoundExceptionItem() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(userOne));
        Assertions.assertThrows(NotFoundException.class, () -> {
            itemService.createComment(new CommentDto(), 1L, 1L);
        }, String.format("Вещь по айди %s не найдена!", 1L));
    }
}