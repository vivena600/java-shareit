package ru.practicum.shareit.item;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithCommentDto;
import ru.practicum.shareit.user.User;

import java.util.List;

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

    public ItemWithCommentDto toItemWithCommentDto(Item item, List<CommentDto> comments,
                                                   BookingDto nextBooking, BookingDto lastBooking) {

        return ItemWithCommentDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .comments(comments)
                .available(item.getAvailable())
                .owner(item.getOwner().getId())
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .build();
    }
}
