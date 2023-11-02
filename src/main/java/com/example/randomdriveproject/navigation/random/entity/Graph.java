package com.example.randomdriveproject.navigation.random.entity;

import lombok.Getter;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class Graph {

    private Map<Nodes, List<Edges>> adjacencyList = new HashMap<>();


    // 노드와 간선을 추가하는 메서드
    public void addEdge(Nodes node1, Nodes node2, double weight) {
        Edges edge1 = new Edges();
        edge1.setNode1(node1);
        edge1.setNode2(node2);
        edge1.setWeight(weight);

        Edges edge2 = new Edges();
        edge2.setNode1(node2);
        edge2.setNode2(node1);
        edge2.setWeight(weight);

        adjacencyList.putIfAbsent(node1, new ArrayList<>());
        adjacencyList.putIfAbsent(node2, new ArrayList<>());
        adjacencyList.get(node1).add(edge1);
        adjacencyList.get(node2).add(edge2);
    }

    // 특정 노드에 연결된 간선들을 가져오는 메서드
    public List<Edges> getEdges(Nodes node) {
        return adjacencyList.getOrDefault(node, new ArrayList<>());
    }

    // 모든 노드들을 가져오는 메서드
    public Set<Nodes> getNodes() {
        return adjacencyList.keySet();
    }


    // 모든 간선들을 가져오는 메서드
    public List<Edges> getAllEdges() {
        List<Edges> allEdges = new ArrayList<>();
        for (List<Edges> edgesList : adjacencyList.values()) {
            allEdges.addAll(edgesList);
        }
        return allEdges;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Graph: \n");
        for (Map.Entry<Nodes, List<Edges>> entry : adjacencyList.entrySet()) {
            sb.append(entry.getKey().toString()).append(": ").append(entry.getValue().toString()).append("\n");
        }
        return sb.toString();
    }

    // 그래프를 초기화하는 메서드
    public void clear() {
        adjacencyList.clear();
    }



}
