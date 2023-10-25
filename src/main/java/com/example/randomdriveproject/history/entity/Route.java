package com.example.randomdriveproject.history.entity;

import com.example.randomdriveproject.user.entity.User;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@NoArgsConstructor
@Table(name = "route")
public class Route extends Timestamped{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "originAddress", nullable = false)
    private String originAddress;

    @Column(name = "destinationAddress", nullable = false)
    private String destinationAddress;

    @Column(name = "mapType", nullable = false)
    private String mapType;

    @Column(name = "duration", nullable = false)
    private int duration;

    @Column(name = "distance", nullable = false)
    private int distance;

    @OneToMany(mappedBy = "route", orphanRemoval = true)
    private List<Bound> bounds = new ArrayList<>();

    @OneToMany(mappedBy = "route", orphanRemoval = true)
    private List<Road> roads = new ArrayList<>();

    @OneToMany(mappedBy = "route", orphanRemoval = true)
    private List<Guide> guides = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user = new User();

    public Route(String originAddress, String destinationAddress, String mapType, int duration, int distance, User user) {
        this.originAddress = originAddress;
        this.destinationAddress = destinationAddress;
        this.mapType = mapType;
        this.duration = duration;
        this.distance = distance;
        this.user = user;
    }
}
