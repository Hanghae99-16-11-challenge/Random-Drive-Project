package com.example.randomdriveproject.history.service;

import com.example.randomdriveproject.history.entity.Bound;
import com.example.randomdriveproject.history.entity.Road;
import com.example.randomdriveproject.history.entity.Route;
import com.example.randomdriveproject.history.repository.BoundRepository;
import com.example.randomdriveproject.history.repository.RoadRepository;
import com.example.randomdriveproject.history.repository.RouteRepository;
import com.example.randomdriveproject.request.dto.KakaoRouteAllResponseDto;
import com.example.randomdriveproject.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HistoryService {

    private final RouteRepository routeRepository;
    private final BoundRepository boundRepository;
    private final RoadRepository roadRepository;

    public void saveHistory(KakaoRouteAllResponseDto requestDto, String originAddress, String destinationAddress, User user) {
        for (KakaoRouteAllResponseDto.RouteInfo routeInfo : requestDto.getRoutes()) {
            KakaoRouteAllResponseDto.Summary summary = routeInfo.getSummary();
            KakaoRouteAllResponseDto.Section section = routeInfo.getSections()[0]; // 첫 번째 Section을 사용하겠습니다.

            // Route 객체 생성
            Route route = new Route(originAddress, destinationAddress, summary.getDuration(), summary.getDistance(), user);

            // Bound 객체 생성 및 설정
            KakaoRouteAllResponseDto.BoundingBox bound = section.getBound();
            Bound boundEntity = new Bound(bound.getMinX(), bound.getMinY(), bound.getMaxX(), bound.getMaxY(),route);

            // Road 객체 생성 및 설정
            KakaoRouteAllResponseDto.Road roadDto = section.getRoads()[0]; // 첫 번째 Road를 사용하겠습니다.
            String vertexesString = Arrays.stream(roadDto.getVertexes()).mapToObj(String::valueOf).collect(Collectors.joining(" "));
            Road roadEntity = new Road(vertexesString, route);

            // RouteRepository를 사용하여 Route 저장
            routeRepository.save(route);
            boundRepository.save(boundEntity);
            roadRepository.save(roadEntity);
        }
    }
}
