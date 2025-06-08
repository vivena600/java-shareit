package ru.practicum.shareit.item;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.user.User;

@UtilityClass
public class ItemMapper {

    public static Item mapItem(ItemDto itemDto, User user /* ,ItemRequest request */) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                //.request(request)
                .owner(user)
                .build();
    }

    public static ItemDto mapItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                //.request(item.getRequest().getId())
                .owner(item.getOwner().getId())
                .build();
    }
}
