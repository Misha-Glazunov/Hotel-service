package org.example.DTO;

import lombok.Data;
import java.time.LocalDate;

@Data
public class AvailabilityRequest {
    private LocalDate startDate;
    private LocalDate endDate;
}
