package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {
    @Autowired
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    @MockBean
    private BookingServiceImpl bookingService;
    private BookingDto firstBookingDto;
    private BookingDto toBookingDto;
    private BookingDtoRequest bookingDtoRequest;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private static final String startTime = "2023-09-21T01:29:22";
    private static final String endTime = "2023-09-22T02:30:22";

    @BeforeEach
    void init() {
        objectMapper = new ObjectMapper();
        firstBookingDto = new BookingDto(1, LocalDateTime.parse(startTime, formatter), LocalDateTime.parse(endTime, formatter), new Item(), new User(), Status.REJECTED);
        bookingDtoRequest = new BookingDtoRequest();
        toBookingDto = new BookingDto(2, LocalDateTime.parse(startTime, formatter), LocalDateTime.parse(endTime, formatter), new Item(), new User(), Status.WAITING);
    }

    @Test
    void createBooking() throws Exception {
        when(bookingService.createBooking(any(), anyLong())).thenReturn(firstBookingDto);
        mockMvc.perform(post("/bookings").header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDtoRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.start").value(firstBookingDto.getStart().toString()))
                .andExpect(jsonPath("$.end").value(firstBookingDto.getEnd().toString()))
                .andExpect(jsonPath("$.status").value("REJECTED"));
    }

    @Test
    void approveBooking() throws Exception {
        when(bookingService.approveBooking(anyLong(), anyBoolean(), anyLong())).thenReturn(firstBookingDto);
        mockMvc.perform(patch("/bookings/{bookingId}", 1).param("approved", "true").header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.start").value(firstBookingDto.getStart().toString()))
                .andExpect(jsonPath("$.end").value(firstBookingDto.getEnd().toString()))
                .andExpect(jsonPath("$.status").value("REJECTED"));
    }

    @Test
    void getBookingById() throws Exception {
        when(bookingService.getBookingById(anyLong(), anyLong())).thenReturn(firstBookingDto);
        mockMvc.perform(get("/bookings/{bookingId}", 1).header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.start").value(firstBookingDto.getStart().toString()))
                .andExpect(jsonPath("$.end").value(firstBookingDto.getEnd().toString()))
                .andExpect(jsonPath("$.status").value("REJECTED"));
    }

    @Test
    void getBookingsByState() throws Exception {
        when(bookingService.getBookingsByState(any(), anyLong())).thenReturn(List.of(firstBookingDto, toBookingDto));
        mockMvc.perform(get("/bookings", 1).param("state", "ALL").header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.[0].id").value(1))
                .andExpect(jsonPath("$.[1].id").value(2))
                .andExpect(jsonPath("$.[0].start").value(firstBookingDto.getStart().toString()))
                .andExpect(jsonPath("$.[0].end").value(firstBookingDto.getEnd().toString()))
                .andExpect(jsonPath("$.[0].status").value("REJECTED"))
                .andExpect(jsonPath("$.[1].start").value(firstBookingDto.getStart().toString()))
                .andExpect(jsonPath("$.[1].end").value(firstBookingDto.getEnd().toString()))
                .andExpect(jsonPath("$.[1].status").value("WAITING"));
    }

    @Test
    void getBookingsByStatePageable() throws Exception {
        when(bookingService.getBookingsByState(any(), anyLong(), anyInt(), anyInt())).thenReturn(List.of(firstBookingDto, toBookingDto));
        mockMvc.perform(get("/bookings", 1).param("state", "ALL").param("from", "0")
                        .param("size", "2").header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.[0].id").value(1))
                .andExpect(jsonPath("$.[1].id").value(2))
                .andExpect(jsonPath("$.[0].start").value(firstBookingDto.getStart().toString()))
                .andExpect(jsonPath("$.[0].end").value(firstBookingDto.getEnd().toString()))
                .andExpect(jsonPath("$.[0].status").value("REJECTED"))
                .andExpect(jsonPath("$.[1].start").value(firstBookingDto.getStart().toString()))
                .andExpect(jsonPath("$.[1].end").value(firstBookingDto.getEnd().toString()))
                .andExpect(jsonPath("$.[1].status").value("WAITING"));
    }

    @Test
    void getBookingsExpectValidError() throws Exception {
        when(bookingService.getBookingsByState(any(), anyLong())).thenReturn(List.of(firstBookingDto, toBookingDto));
        mockMvc.perform(get("/bookings", 1).param("state", "ALL").header("X-Sharer-User-Id", 1)
                        .param("from", "-1")
                        .param("size", "2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void getBookingsByOwnerExpectValidError() throws Exception {
        when(bookingService.getBookingsByState(any(), anyLong())).thenReturn(List.of(firstBookingDto, toBookingDto));
        mockMvc.perform(get("/bookings/owner", 1).param("state", "ALL").header("X-Sharer-User-Id", 1)
                        .param("from", "-1")
                        .param("size", "2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void getBookingsExpectValidPageableNegative() throws Exception {
        when(bookingService.getBookingsByState(any(), anyLong())).thenReturn(List.of(firstBookingDto, toBookingDto));
        mockMvc.perform(get("/bookings", 1).param("state", "NOT_VALID").header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void getBookingsByOwnerOfItemsExpectValidError() throws Exception {
        when(bookingService.getBookingsByOwnerOfItems(any(), anyLong())).thenReturn(List.of(firstBookingDto, toBookingDto));
        mockMvc.perform(get("/bookings/owner", 1).param("state", "NOT_VALID").header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void getBookingsByOwnerOfItemsPageable() throws Exception {
        when(bookingService.getBookingsByOwnerOfItems(any(), anyLong())).thenReturn(List.of(firstBookingDto, toBookingDto));
        mockMvc.perform(get("/bookings/owner", 1).param("state", "ALL").header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.[0].id").value(1))
                .andExpect(jsonPath("$.[1].id").value(2))
                .andExpect(jsonPath("$.[0].start").value(firstBookingDto.getStart().toString()))
                .andExpect(jsonPath("$.[0].end").value(firstBookingDto.getEnd().toString()))
                .andExpect(jsonPath("$.[0].status").value("REJECTED"))
                .andExpect(jsonPath("$.[1].start").value(firstBookingDto.getStart().toString()))
                .andExpect(jsonPath("$.[1].end").value(firstBookingDto.getEnd().toString()))
                .andExpect(jsonPath("$.[1].status").value("WAITING"));
    }

    @Test
    void getBookingsByOwnerOfItems() throws Exception {
        when(bookingService.getBookingsByOwnerOfItems(any(), anyLong(), anyInt(), anyInt())).thenReturn(List.of(firstBookingDto, toBookingDto));
        mockMvc.perform(get("/bookings/owner", 1).param("state", "ALL").param("from", "0")
                        .param("size", "2").header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.[0].id").value(1))
                .andExpect(jsonPath("$.[1].id").value(2))
                .andExpect(jsonPath("$.[0].start").value(firstBookingDto.getStart().toString()))
                .andExpect(jsonPath("$.[0].end").value(firstBookingDto.getEnd().toString()))
                .andExpect(jsonPath("$.[0].status").value("REJECTED"))
                .andExpect(jsonPath("$.[1].start").value(firstBookingDto.getStart().toString()))
                .andExpect(jsonPath("$.[1].end").value(firstBookingDto.getEnd().toString()))
                .andExpect(jsonPath("$.[1].status").value("WAITING"));
    }

    @Test
    void shouldExceptionCreateBooking() throws Exception {
        when(bookingService.createBooking(any(), anyLong())).thenThrow(new Exception());
        mockMvc.perform(post("/bookings").header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDtoRequest)))
                .andExpect(status().is5xxServerError());
    }

}