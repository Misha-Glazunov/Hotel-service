package org.example.Map;

import org.example.DTO.BookingDTO;
import org.example.Entity.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BookingMapper {
    BookingDTO toBookingDTO(Booking booking);
    List<BookingDTO> toBookingDTOList(List<Booking> bookings);
}
