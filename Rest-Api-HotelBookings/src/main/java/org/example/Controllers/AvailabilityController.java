package org.example.Controllers;

import lombok.RequiredArgsConstructor;
import org.example.DTO.AvailabilityRequest;
import org.example.DTO.AvailabilityResponse;
import org.example.Service.AvailabilityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    @PostMapping("/{roomId}/confirm-availability")
    public ResponseEntity<AvailabilityResponse> confirmAvailability(
            @PathVariable Long roomId,
            @RequestBody AvailabilityRequest request) {

        AvailabilityResponse response = availabilityService.confirmAvailability(roomId, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{roomId}/release")
    public ResponseEntity<Void> releaseRoom(
            @PathVariable Long roomId,
            @RequestBody AvailabilityRequest request) {

        availabilityService.releaseRoom(roomId, request);
        return ResponseEntity.ok().build();
    }
}