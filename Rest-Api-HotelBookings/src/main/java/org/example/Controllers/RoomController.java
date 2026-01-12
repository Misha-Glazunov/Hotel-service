package org.example.Controllers;

import org.example.DTO.CreateRoomRequest;
import org.example.DTO.RoomDTO;
import org.example.Service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    @Autowired
    private RoomService roomService;

    @GetMapping
    public List<RoomDTO> getAllRooms(){
        return roomService.getAllRooms();
    }

    @GetMapping ("{id}")
    public RoomDTO getRoomById(@PathVariable Long id){
        return roomService.getRoomById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RoomDTO createRoom(@RequestBody CreateRoomRequest request){
        return roomService.createRoom(request);
    }
}
