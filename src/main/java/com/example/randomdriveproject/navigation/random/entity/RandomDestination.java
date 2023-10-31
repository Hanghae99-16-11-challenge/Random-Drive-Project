package com.example.randomdriveproject.navigation.random.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "randomDestination")
public class RandomDestination {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "userId", nullable = false)
    private Long userId;

    @Column(name = "destinationAddress", nullable = false)
    private String destinationAddress;

    public RandomDestination(Long userId, String destinationAddress) {
        this.userId = userId;
        this.destinationAddress = destinationAddress;
    }
}
