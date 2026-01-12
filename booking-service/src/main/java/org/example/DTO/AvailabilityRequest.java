package org.example.DTO;

import lombok.Data;
import java.time.LocalDate;

@Data
public class AvailabilityRequest {
    private LocalDate startDate;
    private LocalDate endDate;

    // Конструкторы
    public AvailabilityRequest() {}

    public AvailabilityRequest(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }
}