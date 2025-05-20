package ru.practicum.shareit.item.service;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto createItem(Long userId, Item item) {
        item.setOwner(getOwner(userId));
        return itemRepository.createItem(item);
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemId, Item item) {
        User itemOwner = getOwner(userId);
        Item oldItem = ItemMapper.mapItem(itemRepository.getItemById(itemId));
        if (oldItem.getOwner() == null || !oldItem.getOwner().equals(itemOwner)) {
            throw new ValidationException("Пользователь с id = " + userId + "не может редактировать эту вещь");
        }
        item.setOwner(itemOwner);
        return itemRepository.updateItem(itemId, item);
    }

    @Override
    public ItemDto getItem(Long itemId) {
        return itemRepository.getItemById(itemId);
    }

    @Override
    public List<ItemDto> getUserItems(Long userId) {
        return itemRepository.getUserItems(getOwner(userId));
    }

    @Override
    public List<ItemDto> searchItems(Long userId, String text) {
        User owner = getOwner(userId);
        if (text.isEmpty()) {
            return new ArrayList<ItemDto>();
        }
        return itemRepository.searchItems(text);
    }

    private User getOwner(Long userId) {
        return UserMapper.mapUser(userRepository.getUserById(userId));
    }
}
