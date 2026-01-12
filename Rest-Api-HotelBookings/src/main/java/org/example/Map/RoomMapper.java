package org.example.Map;

import org.example.DTO.CreateRoomRequest;
import org.example.DTO.RoomDTO;
import org.example.Entity.Room;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper (componentModel = MappingConstants.ComponentModel.SPRING)
public interface RoomMapper {

    @Mapping(target = "hotelId", source = "hotel.id")
    RoomDTO toRoomDTO (Room room);

    @Mapping(target = "hotelId", source = "hotel.id")
    List<RoomDTO> toRoomDTOList (List<Room> rooms);

    Room toRoom(CreateRoomRequest request);
}
