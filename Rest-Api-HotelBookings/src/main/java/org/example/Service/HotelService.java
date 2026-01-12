package org.example.Service;

import org.example.DTO.CreateHotelRequest;
import org.example.DTO.HotelDTO;
import org.example.Map.HotelMapper;
import org.example.Repository.HotelRepository;
import org.example.Entity.Hotel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HotelService {

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private HotelMapper hotelMapper;

    public List<HotelDTO> getAllHotels(){
        List<Hotel> hotels = hotelRepository.findAll();
        return hotelMapper.toHotelDTOList(hotels);
    }

    public HotelDTO getHotelById(Long id){
        Hotel hotel = hotelRepository.findById(id).orElseThrow();
        return hotelMapper.toHotelDTO(hotel);
    }

    public HotelDTO createHotel(CreateHotelRequest request) {
    Hotel hotel = new Hotel();
        hotel.setName(request.getName());
        hotel.setAddress(request.getAddress());

        Hotel savedHotel = hotelRepository.save(hotel);

        return hotelMapper.toHotelDTO(savedHotel);
    }

}
