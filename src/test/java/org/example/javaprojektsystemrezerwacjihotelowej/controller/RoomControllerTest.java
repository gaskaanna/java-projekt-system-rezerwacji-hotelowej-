package org.example.javaprojektsystemrezerwacjihotelowej.controller;

import org.example.javaprojektsystemrezerwacjihotelowej.entity.Room;
import org.example.javaprojektsystemrezerwacjihotelowej.service.RoomService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RoomControllerTest {

    @Mock
    private RoomService roomService;

    @InjectMocks
    private RoomController roomController;

    private Room testRoom;
    private List<Room> testRooms;

    @BeforeEach
    void setUp() {
        // Create test room
        testRoom = new Room();
        testRoom.setRoomId(1L);
        testRoom.setRoomNumber("101");
        testRoom.setFloor("1");
        testRoom.setNumberOfBeds(2);
        testRoom.setPrice(100.0);

        // Create test room list
        Room room2 = new Room();
        room2.setRoomId(2L);
        room2.setRoomNumber("102");
        room2.setFloor("1");
        room2.setNumberOfBeds(3);
        room2.setPrice(150.0);

        testRooms = Arrays.asList(testRoom, room2);

        // Setup mocks
        when(roomService.getAllRooms()).thenReturn(testRooms);
        when(roomService.getRoomById(1L)).thenReturn(testRoom);
        when(roomService.createRoom(any(Room.class))).thenReturn(testRoom);
        when(roomService.updateRoom(eq(1L), any(Room.class))).thenReturn(testRoom);
        doNothing().when(roomService).deleteRoom(1L);
        when(roomService.findAvailableRooms(any(LocalDate.class), any(LocalDate.class), anyInt())).thenReturn(testRooms);
    }

    @Test
    void getAllRooms_ShouldReturnAllRooms() {
        // Act
        List<Room> result = roomController.getAllRooms();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(testRoom, result.get(0));
        assertEquals(testRooms.get(1), result.get(1));

        // Verify service was called
        verify(roomService, times(1)).getAllRooms();
    }

    @Test
    void createRoom_ShouldCreateAndReturnRoom() {
        // Act
        ResponseEntity<Room> response = roomController.createRoom(testRoom);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testRoom, response.getBody());

        // Verify service was called
        verify(roomService, times(1)).createRoom(testRoom);
    }

    @Test
    void updateRoom_ShouldUpdateAndReturnRoom() {
        // Act
        ResponseEntity<Room> response = roomController.updateRoom(1L, testRoom);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testRoom, response.getBody());

        // Verify service was called
        verify(roomService, times(1)).updateRoom(1L, testRoom);
    }

    @Test
    void deleteRoom_ShouldReturnNoContent() {
        // Act
        ResponseEntity<Void> response = roomController.deleteRoom(1L);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        // Verify service was called
        verify(roomService, times(1)).deleteRoom(1L);
    }

    @Test
    void findAvailable_ShouldReturnAvailableRooms() {
        // Arrange
        LocalDate checkIn = LocalDate.now();
        LocalDate checkOut = LocalDate.now().plusDays(3);
        int beds = 2;

        // Act
        List<Room> result = roomController.findAvailable(checkIn, checkOut, beds);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(testRoom, result.get(0));
        assertEquals(testRooms.get(1), result.get(1));

        // Verify service was called
        verify(roomService, times(1)).findAvailableRooms(checkIn, checkOut, beds);
    }
}
