package org.example.Repository;

import org.example.Entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {
    List<Hotel> findByName(String name);

    List<Hotel> findByAddress(String address);

    Optional<Hotel> findById(Long id);
}
