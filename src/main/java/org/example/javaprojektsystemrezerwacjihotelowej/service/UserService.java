package org.example.javaprojektsystemrezerwacjihotelowej.service;

import lombok.RequiredArgsConstructor;
import org.example.javaprojektsystemrezerwacjihotelowej.entity.User;
import org.example.javaprojektsystemrezerwacjihotelowej.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id " + id));
    }

    @Transactional
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Transactional
    public User updateUser(Long id, User updated) {
        User existing = getUserById(id);
        existing.setUsername(updated.getUsername());
        existing.setUsersurname(updated.getUsersurname());
        existing.setEmail(updated.getEmail());
        existing.setPhone(updated.getPhone());
        existing.setRoleType(updated.getRoleType());
        return userRepository.save(existing);
    }

    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}