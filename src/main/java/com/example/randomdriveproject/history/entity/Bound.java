package com.example.randomdriveproject.history.entity;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Getter
@NoArgsConstructor
@Table(name = "bound")
public class Bound {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "min_x", nullable = false)
    private double minX;

    @Column(name = "min_y", nullable = false)
    private double minY;

    @Column(name = "max_x", nullable = false)
    private double maxX;

    @Column(name = "max_y", nullable = false)
    private double maxY;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id")
    private Route route = new Route();

    public Bound(double minX, double minY, double maxX, double maxY, Route route) {
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
        this.route = route;
    }
}
