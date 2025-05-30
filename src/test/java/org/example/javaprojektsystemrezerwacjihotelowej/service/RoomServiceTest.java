package org.example.javaprojektsystemrezerwacjihotelowej.service;

import org.example.javaprojektsystemrezerwacjihotelowej.entity.Reservation;
import org.example.javaprojektsystemrezerwacjihotelowej.entity.Room;
import org.example.javaprojektsystemrezerwacjihotelowej.repository.ReservationsRepository;
import org.example.javaprojektsystemrezerwacjihotelowej.repository.RoomsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RoomServiceTest {

    @Mock
    private RoomsRepository roomsRepository;

    @Mock
    private ReservationsRepository reservationsRepository;

    @InjectMocks
    private RoomService roomService;

    private Room testRoom;
    private Reservation testReservation;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Create test room
        testRoom = new Room();
        testRoom.setRoomId(1L);
        testRoom.setRoomNumber("101");
        testRoom.setFloor("1");
        testRoom.setNumberOfBeds(2);
        testRoom.setPrice(100.0);
        
        // Create test reservation
        testReservation = new Reservation();
        testReservation.setReservationId(1L);
        testReservation.setRoom(testRoom);
        testReservation.setCheckInDate(LocalDate.now().plusDays(5));
        testReservation.setCheckOutDate(LocalDate.now().plusDays(10));
        
        // Setup mocks
        when(roomsRepository.findById(1L)).thenReturn(Optional.of(testRoom));
        when(roomsRepository.save(any(Room.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(roomsRepository.findAll()).thenReturn(Arrays.asList(testRoom));
        when(roomsRepository.findByNumberOfBedsGreaterThanEqual(anyInt())).thenReturn(Arrays.asList(testRoom));
    }

    @Test
    void getAllRooms_ShouldReturnAllRooms() {
        // Act
        List<Room> result = roomService.getAllRooms();
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testRoom, result.get(0));
        
        // Verify repository was called
        verify(roomsRepository, times(1)).findAll();
    }

    @Test
    void getRoomById_ShouldReturnRoomWhenExists() {
        // Act
        Room result = roomService.getRoomById(1L);
        
        // Assert
        assertNotNull(result);
        assertEquals(testRoom, result);
        
        // Verify repository was called
        verify(roomsRepository, times(1)).findById(1L);
    }

    @Test
    void getRoomById_ShouldThrowExceptionWhenNotExists() {
        // Arrange
        when(roomsRepository.findById(999L)).thenReturn(Optional.empty());
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> roomService.getRoomById(999L)
        );
        
        assertEquals("Room not found with id: 999", exception.getMessage());
        
        // Verify repository was called
        verify(roomsRepository, times(1)).findById(999L);
    }

    @Test
    void createRoom_ShouldSaveAndReturnRoom() {
        // Arrange
        Room newRoom = new Room();
        newRoom.setRoomNumber("102");
        newRoom.setFloor("1");
        newRoom.setNumberOfBeds(3);
        newRoom.setPrice(150.0);
        
        // Act
        Room result = roomService.createRoom(newRoom);
        
        // Assert
        assertNotNull(result);
        assertEquals(newRoom, result);
        
        // Verify repository was called
        verify(roomsRepository, times(1)).save(newRoom);
    }

    @Test
    void updateRoom_ShouldUpdateFieldsAndSave() {
        // Arrange
        Room updatedRoom = new Room();
        updatedRoom.setRoomNumber("101-Updated");
        updatedRoom.setFloor("2");
        updatedRoom.setNumberOfBeds(4);
        updatedRoom.setPrice(200.0);
        
        // Act
        Room result = roomService.updateRoom(1L, updatedRoom);
        
        // Assert
        assertNotNull(result);
        assertEquals("101-Updated", result.getRoomNumber());
        assertEquals("2", result.getFloor());
        assertEquals(4, result.getNumberOfBeds());
        assertEquals(200.0, result.getPrice());
        
        // Verify interactions
        verify(roomsRepository, times(1)).findById(1L);
        
        // Capture the room being saved to verify its properties
        ArgumentCaptor<Room> roomCaptor = ArgumentCaptor.forClass(Room.class);
        verify(roomsRepository).save(roomCaptor.capture());
        Room savedRoom = roomCaptor.getValue();
        
        assertEquals("101-Updated", savedRoom.getRoomNumber());
        assertEquals("2", savedRoom.getFloor());
        assertEquals(4, savedRoom.getNumberOfBeds());
        assertEquals(200.0, savedRoom.getPrice());
    }

    @Test
    void deleteRoom_ShouldDeleteRoom() {
        // Act
        roomService.deleteRoom(1L);
        
        // Verify interactions
        verify(roomsRepository, times(1)).findById(1L);
        verify(roomsRepository, times(1)).delete(testRoom);
    }

    @Test
    void findAvailableRooms_ShouldReturnAvailableRooms() {
        // Arrange
        LocalDate checkIn = LocalDate.now();
        LocalDate checkOut = LocalDate.now().plusDays(3);
        int minBeds = 2;
        
        // No conflicting reservations
        when(reservationsRepository.findByRoomAndCheckInDateLessThanAndCheckOutDateGreaterThan(
            eq(testRoom), eq(checkOut), eq(checkIn)
        )).thenReturn(Collections.emptyList());
        
        // Act
        List<Room> result = roomService.findAvailableRooms(checkIn, checkOut, minBeds);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testRoom, result.get(0));
        
        // Verify interactions
        verify(roomsRepository, times(1)).findByNumberOfBedsGreaterThanEqual(minBeds);
        verify(reservationsRepository, times(1)).findByRoomAndCheckInDateLessThanAndCheckOutDateGreaterThan(
            eq(testRoom), eq(checkOut), eq(checkIn)
        );
    }

    @Test
    void findAvailableRooms_ShouldNotReturnRoomsWithConflictingReservations() {
        // Arrange
        LocalDate checkIn = LocalDate.now();
        LocalDate checkOut = LocalDate.now().plusDays(3);
        int minBeds = 2;
        
        // Conflicting reservation
        when(reservationsRepository.findByRoomAndCheckInDateLessThanAndCheckOutDateGreaterThan(
            eq(testRoom), eq(checkOut), eq(checkIn)
        )).thenReturn(Arrays.asList(testReservation));
        
        // Act
        List<Room> result = roomService.findAvailableRooms(checkIn, checkOut, minBeds);
        
        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());
        
        // Verify interactions
        verify(roomsRepository, times(1)).findByNumberOfBedsGreaterThanEqual(minBeds);
        verify(reservationsRepository, times(1)).findByRoomAndCheckInDateLessThanAndCheckOutDateGreaterThan(
            eq(testRoom), eq(checkOut), eq(checkIn)
        );
    }
}