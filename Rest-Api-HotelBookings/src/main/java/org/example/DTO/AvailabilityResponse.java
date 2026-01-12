package org.example.DTO;

import lombok.Data;

@Data
public class AvailabilityResponse {
    private boolean available;
    private String message;

    public AvailabilityResponse(boolean available, String message) {
        this.available = available;
        this.message = message;
    }

    public static AvailabilityResponse available() {
        return new AvailabilityResponse(true, "Room is available");
    }

    public static AvailabilityResponse notAvailable(String reason) {
        return new AvailabilityResponse(false, reason);
    }
}