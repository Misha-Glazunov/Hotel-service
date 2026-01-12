package org.example.Service;

import lombok.RequiredArgsConstructor;
import org.example.DTO.AvailabilityRequest;
import org.example.DTO.AvailabilityResponse;
import org.example.DTO.BookingDTO;
import org.example.Entity.Booking;
import org.example.Entity.BookingStatus;
import org.example.Entity.User;
import org.example.Map.BookingMapper;
import org.example.Repository.BookingRepository;
import org.example.Repository.UserRepository;
import org.example.Request.BookingRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final BookingMapper bookingMapper;
    private final WebClient webClient;

    public List<BookingDTO> getAllBookings() {
        List<Booking> bookings = bookingRepository.findAll();
        return bookingMapper.toBookingDTOList(bookings);
    }

    public BookingDTO getBookingById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found with id: " + id));
        return bookingMapper.toBookingDTO(booking);
    }

    @Transactional
    public BookingDTO createBooking(BookingRequest request) {
        // Временное решение - используем первого пользователя из базы
        // Позже заменим на получение из аутентификации
        User user = userRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "No users found in system"));

        // 1. Создаем бронирование в статусе PENDING
        Booking booking = Booking.builder()
                .user(user)
                .roomId(request.getRoomId())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .status(BookingStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        Booking savedBooking = bookingRepository.save(booking);

        // 2. Вызываем Hotel Service для подтверждения доступности
        try {
            AvailabilityResponse response = webClient.post()
                    .uri("http://localhost:8081/api/rooms/{roomId}/confirm-availability", request.getRoomId())
                    .bodyValue(new AvailabilityRequest(request.getStartDate(), request.getEndDate()))
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError(), clientResponse -> {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Room availability check failed");
                    })
                    .onStatus(status -> status.is5xxServerError(), clientResponse -> {
                        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Hotel service unavailable");
                    })
                    .bodyToMono(AvailabilityResponse.class)
                    .block(); // Блокируем, так как у нас не реактивное приложение

            if (response != null && response.isAvailable()) {
                // 3. Если доступно, меняем статус на CONFIRMED
                savedBooking.setStatus(BookingStatus.CONFIRMED);
                Booking confirmedBooking = bookingRepository.save(savedBooking);
                return bookingMapper.toBookingDTO(confirmedBooking);
            } else {
                // 4. Если недоступно, отменяем бронирование
                String errorMessage = (response != null) ? response.getMessage() : "Unknown error";
                return cancelBookingWithCompensation(savedBooking, request, "Room is not available: " + errorMessage);
            }

        } catch (Exception e) {
            // 5. В случае ошибки (таймаут, недоступность сервиса) отменяем бронирование
            return cancelBookingWithCompensation(savedBooking, request,
                    "Booking failed due to service error: " + e.getMessage());
        }
    }

    /**
     * Вспомогательный метод для отмены бронирования с компенсирующим действием
     */
    private BookingDTO cancelBookingWithCompensation(Booking booking, BookingRequest request, String errorMessage) {
        // Отменяем бронирование
        booking.setStatus(BookingStatus.CANCELLED);
        Booking cancelledBooking = bookingRepository.save(booking);

        // Пытаемся освободить комнату в Hotel Service (компенсирующее действие)
        try {
            webClient.post()
                    .uri("http://localhost:8081/api/rooms/{roomId}/release", request.getRoomId())
                    .bodyValue(new AvailabilityRequest(request.getStartDate(), request.getEndDate()))
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
        } catch (Exception ex) {
            // Логируем ошибку освобождения, но не прерываем выполнение
            System.err.println("Failed to release room: " + ex.getMessage());
        }

        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
    }

    /**
     * Метод для отмены бронирования пользователем
     */
    @Transactional
    public BookingDTO cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));

        if (booking.getStatus() != BookingStatus.CONFIRMED && booking.getStatus() != BookingStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Cannot cancel booking with status: " + booking.getStatus());
        }

        // Освобождаем комнату в Hotel Service
        try {
            AvailabilityRequest releaseRequest = new AvailabilityRequest(
                    booking.getStartDate(), booking.getEndDate());

            webClient.post()
                    .uri("http://localhost:8081/api/rooms/{roomId}/release", booking.getRoomId())
                    .bodyValue(releaseRequest)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
        } catch (Exception e) {
            System.err.println("Failed to release room during cancellation: " + e.getMessage());
            // Все равно отменяем бронирование, даже если не удалось освободить комнату
        }

        // Меняем статус бронирования
        booking.setStatus(BookingStatus.CANCELLED);
        Booking cancelledBooking = bookingRepository.save(booking);

        return bookingMapper.toBookingDTO(cancelledBooking);
    }

    /**
     * Получить бронирования конкретного пользователя
     */
    public List<BookingDTO> getUserBookings(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        List<Booking> userBookings = bookingRepository.findByUser(user);
        return bookingMapper.toBookingDTOList(userBookings);
    }

    /**
     * Получить бронирования по статусу (для админа)
     */
    public List<BookingDTO> getBookingsByStatus(BookingStatus status) {
        List<Booking> bookings = bookingRepository.findByStatus(status);
        return bookingMapper.toBookingDTOList(bookings);
    }
}