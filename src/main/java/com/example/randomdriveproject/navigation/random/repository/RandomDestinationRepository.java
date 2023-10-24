package com.example.randomdriveproject.navigation.random.repository;

import com.example.randomdriveproject.navigation.random.entity.RandomDestination;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RandomDestinationRepository extends JpaRepository<RandomDestination, Long> {
    RandomDestination findByUsername(String username);
}
