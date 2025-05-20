package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(Long userId, Item item);

    ItemDto updateItem(Long userId, Long itemId, Item item);

    ItemDto getItem(Long itemId);

    List<ItemDto> getUserItems(Long userId);

    List<ItemDto> searchItems(Long userId, String text);
}
