package org.example.Service;

import lombok.RequiredArgsConstructor;

import org.example.DTO.AvailabilityRequest;
import org.example.DTO.AvailabilityResponse;
import org.example.Entity.Room;
import org.example.Entity.RoomBooking;
import org.example.Repository.RoomBookingRepository;
import org.example.Repository.RoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AvailabilityService {

    private final RoomRepository roomRepository;
    private final RoomBookingRepository roomBookingRepository;

    @Transactional
    public AvailabilityResponse confirmAvailability(Long roomId, AvailabilityRequest request) {
        // 1. Находим комнату
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found with id: " + roomId));

        // 2. Проверяем базовую доступность
        if (!room.getAvailable()) {
            return AvailabilityResponse.notAvailable("Room is not available");
        }

        // 3. Проверяем пересечения с существующими бронированиями
        List<RoomBooking> overlappingBookings = roomBookingRepository
                .findOverlappingBookings(roomId, request.getStartDate(), request.getEndDate());

        if (!overlappingBookings.isEmpty()) {
            return AvailabilityResponse.notAvailable("Room is already booked for these dates");
        }

        // 4. Создаем временную блокировку
        RoomBooking booking = new RoomBooking(room, request.getStartDate(), request.getEndDate());
        roomBookingRepository.save(booking);

        // 5. Увеличиваем счетчик бронирований
        room.setTimesBooked(room.getTimesBooked() + 1);
        roomRepository.save(room);

        return AvailabilityResponse.available();
    }

    @Transactional
    public void releaseRoom(Long roomId, AvailabilityRequest request) {
        // Находим и удаляем бронирования для этих дат
        List<RoomBooking> bookings = roomBookingRepository
                .findByRoomIdAndStartDateAndEndDate(roomId, request.getStartDate(), request.getEndDate());

        if (!bookings.isEmpty()) {
            roomBookingRepository.deleteAll(bookings);

            // Уменьшаем счетчик бронирований
            Room room = roomRepository.findById(roomId)
                    .orElseThrow(() -> new RuntimeException("Room not found"));
            room.setTimesBooked(Math.max(0, room.getTimesBooked() - bookings.size()));
            roomRepository.save(room);
        }
    }
}