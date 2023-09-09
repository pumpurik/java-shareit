package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ItemRepository {
    private static ItemMapper itemMapper;
    private Map<Long, Map<Long,Item>> items = new HashMap<>();
    private long id;

    public Map<Long, Map<Long,Item>> getItems() {
        return items;
    }

    public ItemDto createItem(ItemDto itemDto, Long userId, User user){
        itemDto.setId(++id);
        itemDto.setOwner(user);
        items.put(userId,Map.of(itemDto.getId(), ItemMapper.toItem(itemDto)));
        log.info("Вещь добавлена: {}", items.get(userId).get(itemDto.getId()));
        return ItemMapper.toItemDto(items.get(userId).get(itemDto.getId()));
    }

    public ItemDto updateItem (ItemDto itemDto, Long itemId, Long userId){
        itemDto.setId(itemId);
        items.replace(userId, Map.of(itemId,items.get(userId).get(itemId)), Map.of(itemId, ItemMapper.toItemWithBlankFields(itemDto, items.get(userId).get(itemId))));
        return ItemMapper.toItemDto(items.get(userId).get(itemId));
    }

    public ItemDto getItemById (long itemId){
        List<Item> item = items.values().stream()
                .map(item1 -> item1.get(itemId))
                .collect(Collectors.toList());
        return ItemMapper.toItemDto(item.get(0));
    }

    public List<ItemDto> getAllItemsForOwner (long userId){
        return items.get(userId).values().stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    List<ItemDto> getSearchItems (String text){
        return items.values().stream()
                .map(f -> f.values().stream())
                .flatMap(m -> m.filter(f -> (f.getName().toLowerCase().contains(text)
                        || f.getDescription().toLowerCase().contains(text))&&(f.isAvailable())))
                        .map(ItemMapper::toItemDto)
                        .collect(Collectors.toList());

    }

}
