package org.example.javaprojektsystemrezerwacjihotelowej.service;

import lombok.RequiredArgsConstructor;
import org.example.javaprojektsystemrezerwacjihotelowej.entity.Room;
import org.example.javaprojektsystemrezerwacjihotelowej.entity.Reservation;
import org.example.javaprojektsystemrezerwacjihotelowej.repository.RoomsRepository;
import org.example.javaprojektsystemrezerwacjihotelowej.repository.ReservationsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomsRepository roomsRepository;
    private final ReservationsRepository reservationsRepository;

    public List<Room> getAllRooms() {
        return roomsRepository.findAll();
    }

    public Room getRoomById(Long id) {
        return roomsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Room not found with id: " + id));
    }

    @Transactional
    public Room createRoom(Room room) {
        return roomsRepository.save(room);
    }

    @Transactional
    public Room updateRoom(Long id, Room roomDetails) {
        Room room = getRoomById(id);
        room.setRoomNumber(roomDetails.getRoomNumber());
        room.setFloor(roomDetails.getFloor());
        room.setNumberOfBeds(roomDetails.getNumberOfBeds());
        room.setPrice(roomDetails.getPrice());
        return roomsRepository.save(room);
    }

    @Transactional
    public void deleteRoom(Long id) {
        Room room = getRoomById(id);
        roomsRepository.delete(room);
    }

    public List<Room> findAvailableRooms(LocalDate checkIn, LocalDate checkOut, int minBeds) {
        List<Room> candidates = roomsRepository.findByNumberOfBedsGreaterThanEqual(minBeds);
        return candidates.stream()
                .filter(room -> isRoomFree(room, checkIn, checkOut))
                .collect(Collectors.toList());
    }

    private boolean isRoomFree(Room room, LocalDate in, LocalDate out) {
        List<Reservation> conflicts =
                reservationsRepository.findByRoomAndCheckInDateLessThanAndCheckOutDateGreaterThan(
                        room, out, in
                );
        return conflicts.isEmpty();
    }
}