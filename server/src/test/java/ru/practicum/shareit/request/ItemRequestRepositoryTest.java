package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class ItemRequestRepositoryTest {
    @Autowired
    private TestEntityManager em;
    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Test
    void findAllByRequestorId() {
        ItemRequest itemRequest = em.find(ItemRequest.class, 1L);
        List<ItemRequest> allByRequestorId = itemRequestRepository.findAllByRequestorId(1L);
        assertThat(allByRequestorId).isNotEmpty();
        assertThat(allByRequestorId).contains(itemRequest);
    }

    @Test
    void findAllByRequestorIdNot() {
        ItemRequest itemRequest = em.find(ItemRequest.class, 1L);
        Page<ItemRequest> finds = itemRequestRepository.findAllByRequestorIdNot(2L, PageRequest.of(0, 3));
        assertThat(finds).isNotEmpty();
        assertThat(finds).contains(itemRequest);
    }

    @Test
    void findAllById() {
        ItemRequest itemRequest = em.find(ItemRequest.class, 1L);
        List<ItemRequest> allById = itemRequestRepository.findAllById(1L);
        assertThat(allById).isNotEmpty();
        assertThat(allById).contains(itemRequest);
    }
}