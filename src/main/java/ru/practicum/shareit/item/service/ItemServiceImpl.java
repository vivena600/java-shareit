package ru.practicum.shareit.item.service;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto createItem(Long userId, ItemDto item) {
        log.info("Пользователь с id = {} создает вещь {}", userId, item);
        Item itemEntity = ItemMapper.mapItem(item);
        itemEntity.setOwner(getOwner(userId));
        return ItemMapper.mapItemDto(itemRepository.createItem(itemEntity));
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto item) {
        log.info("Пользователь с id = {} обновляет вещь с id {}", userId, itemId);
        User itemOwner = getOwner(userId);
        Item oldItem = itemRepository.getItemById(itemId);
        if (oldItem.getOwner() == null || !oldItem.getOwner().equals(itemOwner)) {
            throw new ValidationException("Пользователь с id = " + userId + "не может редактировать эту вещь");
        }
        item.setOwner(itemOwner);
        Item itemEntity = ItemMapper.mapItem(item);
        return ItemMapper.mapItemDto(itemRepository.updateItem(itemId, itemEntity));
    }

    @Override
    public ItemDto getItem(Long itemId) {
        log.info("Запрос на получение вещи с id {}", itemId);
        return ItemMapper.mapItemDto(itemRepository.getItemById(itemId));
    }

    @Override
    public List<ItemDto> getUserItems(Long userId) {
        log.info("Запрос на получение вещей пользователя с id {}", userId);
        return itemRepository.getUserItems(getOwner(userId))
                .stream().map(ItemMapper::mapItemDto)
                .toList();
    }

    @Override
    public List<ItemDto> searchItems(Long userId, String text) {
        log.info("Пользователь с id {} ищет вещь по запросу text = {}", userId, text);
        getOwner(userId); //выполняет проверку, существует ли такой пользователь или нте
        if (text.isEmpty()) {
            return new ArrayList<ItemDto>();
        }
        return itemRepository.searchItems(text)
                .stream().map(ItemMapper::mapItemDto)
                .toList();
    }

    private User getOwner(Long userId) {
        return userRepository.getUserById(userId);
    }
}
