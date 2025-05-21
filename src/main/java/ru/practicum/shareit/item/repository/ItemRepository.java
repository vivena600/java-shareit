package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.user.User;

import java.util.List;

public interface ItemRepository {
    Item createItem(Item item);

    Item getItemById(Long itemId);

    Item updateItem(Long itemId, Item item);

    List<Item> getUserItems(User user);

    List<Item> searchItems(String text);
}
