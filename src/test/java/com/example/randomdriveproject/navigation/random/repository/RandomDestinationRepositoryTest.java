package com.example.randomdriveproject.navigation.random.repository;

import com.example.randomdriveproject.navigation.random.entity.RandomDestination;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RandomDestinationRepositoryTest {

    @Autowired
    private RandomDestinationRepository randomDestinationRepository;

    @Test
    void findByUserId() {
        // Given
        Long userId = 1L;
        RandomDestination randomDestination = new RandomDestination(userId, "Sample Address");
        randomDestinationRepository.save(randomDestination);

        // When
        RandomDestination foundDestination = randomDestinationRepository.findByUserId(userId);

        // Then
        assertNotNull(foundDestination);
        assertEquals(userId, foundDestination.getUserId());
        assertEquals("Sample Address", foundDestination.getDestinationAddress());
    }

    @Test
    void findByUserId_NotFound() {
        // Given
        Long userId = 2L;

        // When
        RandomDestination foundDestination = randomDestinationRepository.findByUserId(userId);

        // Then
        assertNull(foundDestination);
    }
}