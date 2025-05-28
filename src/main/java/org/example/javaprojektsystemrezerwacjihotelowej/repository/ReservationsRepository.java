package org.example.javaprojektsystemrezerwacjihotelowej.repository;

import org.example.javaprojektsystemrezerwacjihotelowej.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.example.javaprojektsystemrezerwacjihotelowej.entity.Reservation;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservationsRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByRoomAndCheckInDateLessThanAndCheckOutDateGreaterThan(
            Room room,
            LocalDate checkout,
            LocalDate checkin
    );
}
