package org.example.Controllers;

import org.example.DTO.CreateHotelRequest;
import org.example.DTO.HotelDTO;
import org.example.Service.HotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hotels")
public class HotelController {

    @Autowired
    private HotelService hotelService;

    @GetMapping
    public List<HotelDTO> getAllHotels(){
        return hotelService.getAllHotels();
    }

    @GetMapping ("{id}")
    public HotelDTO getHotelById(@PathVariable Long id){
        return hotelService.getHotelById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public HotelDTO createHotel (@RequestBody CreateHotelRequest request){
        return hotelService.createHotel(request);
    }
}
