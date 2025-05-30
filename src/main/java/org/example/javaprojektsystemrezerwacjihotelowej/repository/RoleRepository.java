package org.example.javaprojektsystemrezerwacjihotelowej.repository;

import org.example.javaprojektsystemrezerwacjihotelowej.entity.Role;
import org.example.javaprojektsystemrezerwacjihotelowej.entity.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(RoleName name);
}