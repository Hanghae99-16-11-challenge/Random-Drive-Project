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

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "destinationAddress", nullable = false)
    private String destinationAddress;

    public RandomDestination(String username, String destinationAddress) {
        this.username = username;
        this.destinationAddress = destinationAddress;
    }
}
