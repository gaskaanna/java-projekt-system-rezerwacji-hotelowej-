package org.example.javaprojektsystemrezerwacjihotelowej.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.example.javaprojektsystemrezerwacjihotelowej.entity.Reservation;

@Repository
public interface ReservationsRepository extends JpaRepository<Reservation, Long> {

}
