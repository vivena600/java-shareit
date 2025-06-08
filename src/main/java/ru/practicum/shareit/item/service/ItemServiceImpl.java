package ru.practicum.shareit.item.service;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

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
        User user = getOwner(userId);
        Item itemEntity = ItemMapper.mapItem(item, user);
        itemEntity.setOwner(getOwner(userId));
        return ItemMapper.mapItemDto(itemRepository.save(itemEntity));
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto item) {
        log.info("Пользователь с id = {} обновляет вещь с id {}", userId, itemId);
        User itemOwner = getOwner(userId);
        Item oldItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Не удается найти вещь с id " + itemId));;
        if (oldItem.getOwner() == null || !oldItem.getOwner().equals(itemOwner)) {
            throw new ValidationException("Пользователь с id = " + userId + "не может редактировать эту вещь");
        }

        if (item.getDescription() != null && !item.getDescription().equals(oldItem.getDescription())) {
            oldItem.setDescription(item.getDescription());
        }

        if (item.getName() != null && !item.getName().equals(oldItem.getName())) {
            oldItem.setName(item.getName());
        }

        if (item.getAvailable() != null && !item.getAvailable().equals(oldItem.getAvailable())) {
            oldItem.setAvailable(item.getAvailable());
        }

        return ItemMapper.mapItemDto(itemRepository.save(oldItem));
    }

    @Override
    public ItemDto getItem(Long itemId) {
        log.info("Запрос на получение вещи с id {}", itemId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Не удается найти вещь с id " + itemId));
        return ItemMapper.mapItemDto(item);
    }

    @Override
    public List<ItemDto> getUserItems(Long userId) {
        log.info("Запрос на получение вещей пользователя с id {}", userId);
        return itemRepository.findByOwner(getOwner(userId))
                .stream().map(ItemMapper::mapItemDto)
                .toList();
    }

    @Override
    public List<ItemDto> searchItems(Long userId, String text) {
        log.info("Пользователь с id {} ищет вещь по запросу text = {}", userId, text);
        getOwner(userId); //выполняет проверку, существует ли такой пользователь или нет
        if (text.isEmpty()) {
            return new ArrayList<ItemDto>();
        }
        return itemRepository.searchItems(text)
                .stream().map(ItemMapper::mapItemDto)
                .toList();
    }

    private User getOwner(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Не удалось найти пользователя с id " + userId));
    }
}
