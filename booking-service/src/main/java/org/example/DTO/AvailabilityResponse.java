package org.example.DTO;

import lombok.Data;

@Data
public class AvailabilityResponse {
    private boolean available;
    private String message;
}