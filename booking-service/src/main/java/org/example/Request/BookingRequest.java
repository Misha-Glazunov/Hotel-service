package org.example.Request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class BookingRequest {
    private Long roomId;
    private Boolean autoSelect;
    private LocalDate startDate;
    private LocalDate endDate;

}
