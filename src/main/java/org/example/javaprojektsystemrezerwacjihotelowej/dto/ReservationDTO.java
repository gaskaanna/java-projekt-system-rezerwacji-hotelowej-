package org.example.javaprojektsystemrezerwacjihotelowej.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;


public record ReservationDTO(
    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate checkInDate,
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate checkOutDate,
    
    String specialRequests,
    
    Long userId,
    
    Long roomId
) {}