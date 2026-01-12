package org.example.Repository;

import org.example.Entity.Room;
import org.example.Entity.RoomBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RoomBookingRepository extends JpaRepository<RoomBooking, Long> {


    @Query("SELECT rb FROM RoomBooking rb WHERE rb.room.id = :roomId AND " +
            "(:startDate < rb.endDate AND :endDate > rb.startDate)")
    List<RoomBooking> findOverlappingBookings(@Param("roomId") Long roomId,
                                              @Param("startDate") LocalDate startDate,
                                              @Param("endDate") LocalDate endDate);


    List<RoomBooking> findByRoomIdAndStartDateAndEndDate(Long roomId, LocalDate startDate, LocalDate endDate);
}