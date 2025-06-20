package ru.practicum.shareit.request;

import org.mapstruct.Mapper;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.user.User;

@Mapper(componentModel = "spring", uses = {ItemMapper.class, })
public interface RequestMapper {

    default RequestDto mapRequestDto(ItemRequest request) {
        return RequestDto.builder()
                .id(request.getId())
                .created(request.getCreated())
                .description(request.getDescription())
                .userId(request.getRequestor().getId())
                .build();
    }

    default ItemRequest mapItemRequest(RequestDto requestDto, User user) {
        return ItemRequest.builder()
                .id(requestDto.getId())
                .requestor(user)
                .description(requestDto.getDescription())
                .created(requestDto.getCreated())
                .build();
    }
}
