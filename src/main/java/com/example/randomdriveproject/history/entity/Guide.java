package com.example.randomdriveproject.history.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "guide")
public class Guide {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "x", nullable = false)
    private double x;

    @Column(name = "y", nullable = false)
    private double y;

    @Column(name = "distance")
    private int distance;

    @Column(name = "duration")
    private int duration;

    @Column(name = "type")
    private int type;

    @Column(name = "guidance")
    private String guidance;

    @Column(name = "roadIndex")
    private int roadIndex;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id")
    private Route route = new Route();

    public Guide(String name, double x, double y, int distance, int duration, int type, String guidance, int roadIndex, Route route) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.distance = distance;
        this.duration = duration;
        this.type = type;
        this.guidance = guidance;
        this.roadIndex = roadIndex;
        this.route = route;
    }
}
