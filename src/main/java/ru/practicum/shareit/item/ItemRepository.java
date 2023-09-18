package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Optional<Item> findByIdAndOwnerId(Long itemId, Long userId);

    @Query("SELECT it FROM Item it WHERE (LOWER(it.name) LIKE LOWER(CONCAT('%', :text, '%')) OR LOWER(it.description) LIKE LOWER(CONCAT('%', :text, '%'))) AND it.available = true")
    List<Item> search(@Param("text") String text);

    List<Item> findAllByOwnerId(Long userId);
}
