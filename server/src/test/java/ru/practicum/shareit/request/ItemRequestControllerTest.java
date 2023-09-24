package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(ItemRequestController.class)
public class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemRequestService itemRequestService;

    @Test
    public void testCreateRequest() throws Exception {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("Test Request Description");

        when(itemRequestService.createRequest(any(ItemRequestDto.class), any(Long.class))).thenReturn(itemRequestDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Test Request Description"));
    }

    @Test
    public void testGetRequestsWithAnswers() throws Exception {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(1L);
        itemRequestDto.setDescription("Test Request Description");

        when(itemRequestService.getRequestsWithAnswers(any(Long.class)))
                .thenReturn(Collections.singletonList(itemRequestDto));

        mockMvc.perform(MockMvcRequestBuilders.get("/requests")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id").value("1"))
                .andExpect(jsonPath("$.[0].description").value("Test Request Description"));
    }

    @Test
    public void testGetAllRequestsPageable() throws Exception {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(1L);
        itemRequestDto.setDescription("Test Request Description");

        when(itemRequestService.getAllRequests(any(Long.class), anyInt(), anyInt()))
                .thenReturn(Collections.singletonList(itemRequestDto));

        mockMvc.perform(MockMvcRequestBuilders.get("/requests/all")
                        .param("from", "0")
                        .param("size", "10")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value("1"))
                .andExpect(jsonPath("$.[0].id").value("1"))
                .andExpect(jsonPath("$.[0].description").value("Test Request Description"));
    }

    @Test
    public void testGetAllRequests() throws Exception {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(1L);
        itemRequestDto.setDescription("Test Request Description");

        when(itemRequestService.getAllRequests(any(Long.class), anyInt(), anyInt()))
                .thenReturn(Collections.singletonList(itemRequestDto));

        mockMvc.perform(MockMvcRequestBuilders.get("/requests/all")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value("0"));
    }

    @Test
    public void testGetRequestById() throws Exception {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(1L);
        itemRequestDto.setDescription("Test Request Description");

        when(itemRequestService.getRequestById(any(Long.class), any(Long.class)))
                .thenReturn(itemRequestDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/requests/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.description").value("Test Request Description"));
    }
}