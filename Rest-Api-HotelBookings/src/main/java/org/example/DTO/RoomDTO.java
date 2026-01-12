package org.example.DTO;

import lombok.Data;

@Data
public class RoomDTO {
    private String number;
    private Long hotelId;
    private Boolean available;
    private Integer timesBooked;
}
