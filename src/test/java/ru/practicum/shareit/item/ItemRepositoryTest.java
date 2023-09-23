package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class ItemRepositoryTest {
    @Autowired
    private TestEntityManager em;
    @Autowired
    private ItemRepository itemRepository;

    @Test
    void findByIdAndOwnerId() {
        Item item = em.find(Item.class, 1L);
        Optional<Item> byIdAndOwnerId = itemRepository.findByIdAndOwnerId(1L, 1L);
        assertThat(byIdAndOwnerId).isPresent();
        assertThat(byIdAndOwnerId.get()).isEqualTo(item);
    }

    @Test
    @Transactional
    void search() {
        List<Item> finds = itemRepository.search("щётка");
        assertThat(finds).isNotEmpty();
    }


    @Test
    void findAllByOwnerIdPageable() {
        Item item = em.find(Item.class, 1L);
        List<Item> finds = itemRepository.findAllByOwnerId(1L, PageRequest.of(0, 1));
        assertThat(finds).isNotEmpty();
        assertThat(finds.get(0)).isEqualTo(item);
    }
}