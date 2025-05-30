package org.example.javaprojektsystemrezerwacjihotelowej.entity;

import java.util.HashSet;
import java.util.Set;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long user_id;

    private String username;
    private String usersurname;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    private Long phone;

    @Builder.Default
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns        = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

   @OneToMany(mappedBy = "user")
    private Set<Reservation> reservations;


    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

   @ManyToMany
    @JoinTable(
          name = "user_rooms",
          joinColumns = @JoinColumn(name = "user_id"),
          inverseJoinColumns = @JoinColumn(name = "room_id")
     )
    private Set<Room> rooms;

    @PrePersist
    private void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
