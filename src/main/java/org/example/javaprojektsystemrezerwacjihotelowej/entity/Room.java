package org.example.javaprojektsystemrezerwacjihotelowej.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "rooms")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomId;

    @Column(nullable = false)
    private String roomNumber;

    private String floor;

    @Column(nullable = false)
    private int numberOfBeds;

    private double price;

    @ManyToMany(mappedBy = "rooms")
    private Set<User> users;

    @OneToMany(mappedBy = "room")
    private List<Reservation> reservations;
}
