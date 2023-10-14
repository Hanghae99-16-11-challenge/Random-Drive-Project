package com.example.randomdriveproject.randomNavi.direction.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "direction")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class Direction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 출발지
    private String inputAddress;
    private double inputLatitude;
    private double inputLongitude;

    // 도착지
    private String targetAddress;
    private double targetLatitude;
    private double targetLongitude;

    // 거리
    private double distance;


}
