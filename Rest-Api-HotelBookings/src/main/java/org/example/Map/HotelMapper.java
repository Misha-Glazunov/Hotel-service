package org.example.Map;

import org.example.DTO.CreateHotelRequest;
import org.example.DTO.HotelDTO;
import org.example.Entity.Hotel;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper (componentModel = MappingConstants.ComponentModel.SPRING)
public interface HotelMapper {
    HotelDTO toHotelDTO(Hotel hotel);
    List<HotelDTO> toHotelDTOList(List<Hotel> hotels);

    Hotel toHotel(CreateHotelRequest request);
}
