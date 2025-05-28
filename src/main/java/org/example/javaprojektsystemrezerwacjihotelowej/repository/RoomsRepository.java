package org.example.javaprojektsystemrezerwacjihotelowej.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.example.javaprojektsystemrezerwacjihotelowej.entity.Room;

import java.util.List;

@Repository
public interface RoomsRepository extends JpaRepository<Room, Long> {

    List<Room> findByNumberOfBedsGreaterThanEqual(int beds);
}

