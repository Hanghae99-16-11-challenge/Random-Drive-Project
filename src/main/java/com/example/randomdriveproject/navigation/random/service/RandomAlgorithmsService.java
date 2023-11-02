package com.example.randomdriveproject.navigation.random.service;

import com.example.randomdriveproject.navigation.random.entity.*;
import com.example.randomdriveproject.navigation.random.repository.EdgesRepository;
import com.example.randomdriveproject.navigation.random.repository.NodesRepository;
import com.example.randomdriveproject.request.dto.DocumentDto;
import com.example.randomdriveproject.request.dto.KakaoApiResponseDto;
import com.example.randomdriveproject.request.service.KakaoAddressSearchService;
import com.example.randomdriveproject.request.service.RandomKakaoCategorySearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j(topic = "RandomAlgorithmsService")
@Service
@RequiredArgsConstructor
public class RandomAlgorithmsService {

    private final KakaoAddressSearchService kakaoAddressSearchService;
    private final RandomKakaoCategorySearchService kakaoCategorySearchService;

    private final NodesRepository nodesRepository;
    private final EdgesRepository edgesRepository;

    @Value("${kakao.rest.api.key}")
    private String kakaoRestApiKey;

    public RouteResult requestAllRandomWay(String originAddress, Integer radius) {

        DocumentDto origin = kakaoAddressSearchService.requestAddressSearch(originAddress).getDocumentDtoList().get(0);

        KakaoApiResponseDto tourResponses = kakaoCategorySearchService.requestAttractionCategorySearch(origin.getLatitude(), origin.getLongitude(), radius);
        List<DocumentDto> waypoints = new ArrayList<>();

        DocumentDto saveOrigin = new DocumentDto(origin);
        waypoints.add(saveOrigin);  // origin을 waypoints의 첫 번째 요소로 추가
        waypoints.addAll(tourResponses.getDocumentDtoList());

        logWaypointInfo("waypoints", waypoints);
//        logWaypointInfo("getDocumentDtoList placeNames", tourResponses.getDocumentDtoList());
//        logWaypointInfo("getDocumentDtoList addressNames", tourResponses.getDocumentDtoList());

        Graph graph = buildGraphWithWaypoints(waypoints);
        Nodes newOrigin = nodesRepository.findByName("출발지");
        Map<Nodes, Nodes> predecessors = new HashMap<>();

        DijkstraResult dijkstraResult = dijkstra(graph, newOrigin, predecessors);
        List<Nodes> pathNodesList = dijkstraResult.getPathNodes();

        log.info("최적 경로 : {}", pathNodesList.stream().map(Nodes::toString).collect(Collectors.joining(" -> ")));

        return RouteResult.builder()
                .start(origin)
                .destination(pathNodesList.get(2).toDto()) // 마지막 노드 (목적지)
                .waypoints(pathNodesList.get(1).toDto()) // 중간 경유지
                .totalDistance(dijkstraResult.getTotalDistance())
                .path(pathNodesList) // 모든 경로 노드들 (출발지, 중간 경유지, 목적지 포함)
                .build();
    }




    // 경유지 목록 로그로 찍기.
    private void logWaypointInfo(String label, List<DocumentDto> waypoints) {
        log.info("{} : {}", label, waypoints.stream().map(DocumentDto::getPlaceName).collect(Collectors.joining(", ")));
    }




    // 노드와 간선을 그래프로 만들기 -> 그래프
    public Graph buildGraphWithWaypoints(List<DocumentDto> waypoints) {
        Graph graph = new Graph();
        Set<Edges> edgesSet = new HashSet<>();
        List<Nodes> nodesList = waypoints.stream().map(Nodes::new).collect(Collectors.toList());

        for (int i = 0; i < nodesList.size(); i++) {
            for (int j = i + 1; j < nodesList.size(); j++) {
                Nodes node1 = nodesList.get(i);
                Nodes node2 = nodesList.get(j);
                double distance = calculateDistance(
                        node1.getY(), node1.getX(),
                        node2.getY(), node2.getX());

                Edges newEdge = new Edges(node1, node2, distance);
                if(!edgesSet.contains(newEdge)) {
                    graph.addEdge(node1, node2, distance);
                    edgesSet.add(newEdge);
                }
            }
        }
        saveGraphToDatabase(graph);
        log.info("그래프 : {}", graph );
        return graph;
    }


    // Dijkstra 알고리즘
    public DijkstraResult dijkstra(Graph graph, Nodes start, Map<Nodes, Nodes> predecessors) {
        Map<Nodes, Double> shortestDistances = new HashMap<>();
//        Map<Nodes, Nodes> predecessors = new HashMap<>();
//        PriorityQueue<Nodes> nodes = new PriorityQueue<>(Comparator.comparingDouble(shortestDistances::get));
        PriorityQueue<Nodes> nodes = new PriorityQueue<>(Comparator.comparingDouble(node ->
                shortestDistances.getOrDefault(node, Double.MAX_VALUE)));
        nodes.add(start);
        shortestDistances.put(start, 0.0);

        while (!nodes.isEmpty()) {
            Nodes currentNode = nodes.poll();
            for (Edges edge : graph.getEdges(currentNode)) {
                Nodes adjacentNode = (edge.getNode1().equals(currentNode)) ? edge.getNode2() : edge.getNode1();
                double newDist = shortestDistances.get(currentNode) + edge.getWeight();
                if (newDist < shortestDistances.getOrDefault(adjacentNode, Double.MAX_VALUE)) {
                    nodes.add(adjacentNode);
                    shortestDistances.put(adjacentNode, newDist);
                    predecessors.put(adjacentNode, currentNode);  // 경로 추적을 위해 추가
                }
            }
        }
        log.info("알고리즘 : {}", shortestDistances);
        // Map<Nodes, Double> 반환 타입 -> shortestDistances -> 출발지로부터 모든 노드의 경로를 계산한 값이 출력
        Map.Entry<Nodes, Double> maxEntry = Collections.max(shortestDistances.entrySet(),
                Map.Entry.comparingByValue());


        Nodes farthestNode = maxEntry.getKey();

        // 중간 거리의 노드 찾기
        double halfDistance = maxEntry.getValue() / 2.0;
        Nodes middleNode = null;
        double minDifference = Double.MAX_VALUE;

        for (Map.Entry<Nodes, Double> entry : shortestDistances.entrySet()) {
            double difference = Math.abs(halfDistance - entry.getValue());
            if (difference < minDifference) {
                minDifference = difference;
                middleNode = entry.getKey();
            }
        }

        log.info("가장 먼 거리를 가진 노드: {}", farthestNode);
        log.info("중간 거리를 가진 노드: {}", middleNode);

        List<Nodes> pathNodes = new ArrayList<>();
        pathNodes.add(start); // 출발지 추가
        pathNodes.add(middleNode); // 중간 경유지 추가
        pathNodes.add(farthestNode); // 목적지 추가

        double distanceFromStartToMiddle = shortestDistances.get(middleNode);
        double distanceFromMiddleToDestination = shortestDistances.get(farthestNode) - distanceFromStartToMiddle;
        double totalDistance = distanceFromStartToMiddle + distanceFromMiddleToDestination;

        log.info("출발지에서 중간지점까지의 거리: {}", distanceFromStartToMiddle);
        log.info("중간지점에서 목적지까지의 거리: {}", distanceFromMiddleToDestination);
        log.info("출발지에서 목적지까지의 총 거리: {}", totalDistance);


        DijkstraResult result = new DijkstraResult();
        result.setPathNodes(pathNodes);
        result.setTotalDistance(totalDistance);

        return result;
    }

    // haversine fomula 거리계산 알고리즘
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        lat1 = Math.toRadians(lat1);
        lon1 = Math.toRadians(lon1);
        lat2 = Math.toRadians(lat2);
        lon2 = Math.toRadians(lon2);

        double earthRadius = 6371; //Kilometers
        return earthRadius * Math.acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));
    }


    // 경로 추적
    public List<Nodes> findPath(Nodes destination, Map<Nodes, Nodes> predecessors) {
        List<Nodes> path = new LinkedList<>();
        Nodes step = destination;
        if (predecessors.get(step) == null) {
            return null; // 경로가 존재하지 않음
        }
        path.add(step);
        while (predecessors.get(step) != null) {
            step = predecessors.get(step);
            path.add(step);
        }
        Collections.reverse(path);
        return path;
    }

    // 노드 엣지 저장
    private void saveGraphToDatabase(Graph graph) {
        for (Nodes node : graph.getNodes()) {
            nodesRepository.save(node);
        }

        for (Edges edge : graph.getAllEdges()) {
            edgesRepository.save(edge);
        }
    }

}
