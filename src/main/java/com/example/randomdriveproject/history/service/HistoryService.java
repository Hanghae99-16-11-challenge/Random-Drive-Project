package com.example.randomdriveproject.history.service;

import com.example.randomdriveproject.history.dto.AllHistoryResponseDto;
import com.example.randomdriveproject.history.dto.HistoryResponseDto;
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
import java.util.List;
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

    public List<AllHistoryResponseDto> getAllHistories(Long userId) {
        List<Route> routes = routeRepository.findAllByUserId(userId);

        return routes.stream()
                .map(route -> new AllHistoryResponseDto(
                        route.getId(),
                        route.getOriginAddress(),
                        route.getDestinationAddress(),
                        route.getDuration(),
                        route.getDistance(),
                        route.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }

    public HistoryResponseDto getHistory(Long routeId) {
        // RouteId를 이용해 Route 정보를 가져온다고 가정
        Route route = routeRepository.findById(routeId).orElse(null);

        if (route == null) {
            // 해당 routeId에 대한 정보가 없을 경우 예외 처리 또는 적절한 응답을 반환
            throw new IllegalArgumentException("Route not found with ID: " + routeId);
        }

        // Bound, Road 정보 가져오기
        Bound bound = route.getBounds().get(0); // 예시로 첫 번째 Bound 가져옴
        Road road = route.getRoads().get(0); // 예시로 첫 번째 Road 가져옴

        // HistoryResponseDto 객체 생성 및 설정
        HistoryResponseDto.Bound boundDto = new HistoryResponseDto.Bound(bound.getMinX(), bound.getMinY(), bound.getMaxX(), bound.getMaxY());
        double[] vertexArray = convertStringToList(road.getVertexes()); // Road의 vertexes를 double 배열로 변환
        HistoryResponseDto.Road roadDto = new HistoryResponseDto.Road(vertexArray);

        HistoryResponseDto responseDto = new HistoryResponseDto();
        responseDto.setOriginAddress(route.getOriginAddress());
        responseDto.setDestinationAddress(route.getDestinationAddress());
        responseDto.setDuration(route.getDuration());
        responseDto.setDistance(route.getDistance());
        responseDto.setCreatedAt(route.getCreatedAt());
        responseDto.setBounds(boundDto);
        responseDto.setRoads(roadDto);

        return responseDto;
    }


    private double[] convertStringToList(String vertexesString) {
        String[] vertexStrings = vertexesString.split(" ");
        double vertexArray[] = new double[vertexStrings.length];;
        for (int i = 0; i < vertexStrings.length; i++) {
            vertexArray[i] = Double.parseDouble(vertexStrings[i]);
        }
        return vertexArray;
    }

}
