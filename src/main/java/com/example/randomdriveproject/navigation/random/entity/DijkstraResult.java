package com.example.randomdriveproject.navigation.random.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DijkstraResult {
    private List<Nodes> pathNodes;
    private double totalDistance;
}
