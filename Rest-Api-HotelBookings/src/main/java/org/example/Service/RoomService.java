package org.example.Service;

import org.example.DTO.CreateRoomRequest;
import org.example.DTO.RoomDTO;
import org.example.Map.RoomMapper;
import org.example.Repository.HotelRepository;
import org.example.Repository.RoomRepository;
import org.example.Entity.Hotel;
import org.example.Entity.Room;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomService {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private RoomMapper roomMapper;

    public List<RoomDTO> getAllRooms(){
        List<Room> rooms = roomRepository.findAll();
        return roomMapper.toRoomDTOList(rooms);
    }

    public RoomDTO getRoomById(Long id){
        Room room = roomRepository.findById(id).orElseThrow();
        return roomMapper.toRoomDTO(room);
    }

    public RoomDTO createRoom(CreateRoomRequest request) {
        Hotel hotel = hotelRepository.findById(request.getHotelId())
                .orElseThrow(() -> new RuntimeException("Отель не найден"));

        Room room = new Room();
        room.setNumber(request.getNumber());
        room.setHotel(hotel);
        room.setAvailable(true);
        room.setTimesBooked(0);

        Room savedRoom = roomRepository.save(room);
        return roomMapper.toRoomDTO(savedRoom);
    }

}
