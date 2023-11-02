package com.example.randomdriveproject.navigation.random.entity;

import com.example.randomdriveproject.request.dto.DocumentDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RouteResult {

    private DocumentDto start;
    private DocumentDto destination;
    private DocumentDto waypoints;
    private double totalDistance;
    private List<Nodes> path; // 출발지에서 목적지까지의 노드 순서대로의 경로
}
