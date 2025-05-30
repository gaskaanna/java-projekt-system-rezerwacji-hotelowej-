package org.example.javaprojektsystemrezerwacjihotelowej.config;

import org.example.javaprojektsystemrezerwacjihotelowej.entity.Role;
import org.example.javaprojektsystemrezerwacjihotelowej.entity.RoleName;
import org.example.javaprojektsystemrezerwacjihotelowej.entity.User;
import org.example.javaprojektsystemrezerwacjihotelowej.repository.RoleRepository;
import org.example.javaprojektsystemrezerwacjihotelowej.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final RoleRepository roleRepo;
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner seedDatabase() {
        return args -> {
            for (RoleName rn : RoleName.values()) {
                roleRepo.findByName(rn).orElseGet(() ->
                        roleRepo.save(Role.builder().name(rn).build()));
            }
            if (!userRepo.existsByEmail("admin@example.com")) {
                var adminRole = roleRepo.findByName(RoleName.ADMIN).orElseThrow();
                userRepo.save(
                        User.builder()
                                .email("admin@example.com")
                                .password(passwordEncoder.encode("admin123"))
                                .roles(Set.of(adminRole))
                                .build()
                );
            }
        };
    }
}