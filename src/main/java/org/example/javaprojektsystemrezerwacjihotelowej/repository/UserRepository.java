package org.example.javaprojektsystemrezerwacjihotelowej.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.example.javaprojektsystemrezerwacjihotelowej.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

}
