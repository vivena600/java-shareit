package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.user.User;

import java.util.List;

public interface ItemRepository {
    ItemDto createItem(Item item);
    ItemDto getItemById(Long itemId);
    ItemDto updateItem(Long itemId, Item item);
    List<ItemDto> getUserItems(User user);
    List<ItemDto> searchItems(String text);
}
