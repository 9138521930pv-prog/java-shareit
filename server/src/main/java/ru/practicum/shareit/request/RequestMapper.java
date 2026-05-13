package ru.practicum.shareit.request;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestAnswerDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RequestMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "requestorId", target = "requestorId")
    @Mapping(source = "created", target = "created")
    //@Mapping(source = "items", target = "items")
    ItemRequestDto mapToDto(ItemRequest itemRequest);

    List<ItemRequestDto> mapToDto(List<ItemRequest> itemRequests);

    @Mapping(source = "id", target = "itemId")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "owner.id", target = "ownerId")
    RequestAnswerDto mapToRequestAnswerDto(Item item);

    List<RequestAnswerDto> mapToRequestAnswerDto(List<Item> itemList);
}

/*package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestAnswerDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RequestMapper {
    public ItemRequestDto mapToDto(ItemRequest itemRequest) {
        ItemRequestDto itemRequestDto = new ItemRequestDto();

        itemRequestDto.setId(itemRequest.getId());
        itemRequestDto.setDescription(itemRequest.getDescription());
        itemRequestDto.setRequestorId(itemRequest.getRequestorId());
        itemRequestDto. setCreated(itemRequest.getCreated());

        return itemRequestDto;
    }

    public List<ItemRequestDto> mapToDto(List<ItemRequest> itemRequests) {
        return itemRequests.stream().map(request -> mapToDto(request)).toList();
    }

    public RequestAnswerDto mapToRequestAnswerDto(Item item) {
        RequestAnswerDto requestAnswerDto = new RequestAnswerDto();

        requestAnswerDto.setItemId(item.getId());
        requestAnswerDto.setName(item.getName());
        requestAnswerDto.setOwnerId(item.getOwner().getId());

        return requestAnswerDto;
    }

    public List<RequestAnswerDto> mapToRequestAnswerDto(List<Item> itemList) {
        return itemList.stream().map(item -> mapToRequestAnswerDto(item)).toList();
    }
}



 */