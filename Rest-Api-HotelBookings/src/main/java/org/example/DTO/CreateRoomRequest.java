package org.example.DTO;

import lombok.Data;

@Data

public class CreateRoomRequest {
    private String number;
    private Long hotelId;
}
