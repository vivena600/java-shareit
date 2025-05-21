package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();

    @Override
    public Item createItem(Item item) {
        item.setId(getNextId());
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item getItemById(Long itemId) {
        return Optional.ofNullable(items.get(itemId))
                .orElseThrow(() -> new NotFoundException("Не найдена вещь с id: " + itemId));
    }

    @Override
    public Item updateItem(Long itemId, Item item) {
        Item oldItem = getItemById(itemId);

        if (item.getDescription() != null && !item.getDescription().equals(oldItem.getDescription())) {
            oldItem.setDescription(item.getDescription());
        }

        if (item.getName() != null && !item.getName().equals(oldItem.getName())) {
            oldItem.setName(item.getName());
        }

        if (item.getAvailable() != null && !item.getAvailable().equals(oldItem.getAvailable())) {
            oldItem.setAvailable(item.getAvailable());
        }
        return oldItem;
    }

    @Override
    public List<Item> getUserItems(User user) {
        return items.values().stream()
                .filter(item -> item.getOwner().equals(user))
                .toList();
    }

    @Override
    public List<Item> searchItems(String text) {
        text = text.toLowerCase();
        String finalText = text;
        return items.values().stream()
                .filter(item -> item.getAvailable() &&
                        item.getName() != null && (
                                item.getName().toLowerCase().contains(finalText) ||
                                item.getDescription().toLowerCase().contains(finalText)))
                .toList();
    }

    private Long getNextId() {
        return 1 + items.keySet().stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0L);
    }
}
