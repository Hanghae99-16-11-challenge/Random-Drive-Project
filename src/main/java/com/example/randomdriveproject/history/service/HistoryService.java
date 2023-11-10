package com.example.randomdriveproject.history.service;

import com.example.randomdriveproject.history.dto.AllHistoryResponseDto;
import com.example.randomdriveproject.history.dto.HistoryResponseDto;
import com.example.randomdriveproject.history.entity.Bound;
import com.example.randomdriveproject.history.entity.Guide;
import com.example.randomdriveproject.history.entity.Road;
import com.example.randomdriveproject.history.entity.Route;
import com.example.randomdriveproject.history.repository.*;
import com.example.randomdriveproject.navigation.random.entity.RandomDestination;
import com.example.randomdriveproject.navigation.random.repository.RandomDestinationRepository;
import com.example.randomdriveproject.request.dto.KakaoRouteAllResponseDto;
import com.example.randomdriveproject.user.entity.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HistoryService {

    private final RouteRepository routeRepository;
    private final BoundRepository boundRepository;
    private final RoadRepository roadRepository;
    private final RandomDestinationRepository randomDestinationRepository;
    private final GuideRepository guideRepository;

    private final RoadJDBCRepository roadJDBCRepository;
    private final GuideJDBCRepository guideJDBCRepository;

    public void saveHistory(KakaoRouteAllResponseDto requestDto, String originAddress, String destinationAddress, String mapType, User user) {

        if (ObjectUtils.isEmpty(originAddress) || ObjectUtils.isEmpty(requestDto) || ObjectUtils.isEmpty(destinationAddress) || ObjectUtils.isEmpty(mapType)) {
            throw new IllegalArgumentException("출발지 또는 목적지 주소 또는 도로 정보가 전달되지 않았습니다.");
        }

        for (KakaoRouteAllResponseDto.RouteInfo routeInfo : requestDto.getRoutes()) {
            KakaoRouteAllResponseDto.Summary summary = routeInfo.getSummary();
            KakaoRouteAllResponseDto.Section firstSection = routeInfo.getSections()[0]; // 첫 번째 Section을 사용하겠습니다.

            if (mapType.equals("live-all-random")) {
                RandomDestination olderRandomDestination = randomDestinationRepository.findByUserId(user.getId());
                if (olderRandomDestination == null) {
                    throw new EntityNotFoundException("경로를 저장할 수 없습니다.");
                }
                destinationAddress = olderRandomDestination.getDestinationAddress();
            }

            // Route 객체 생성
            Route route = new Route(originAddress, destinationAddress, mapType, summary.getDuration(), summary.getDistance(), user);

            // Bound 객체 생성 및 설정
            KakaoRouteAllResponseDto.BoundingBox bound = firstSection.getBound();

            double min_x = bound.getMinX();
            double min_y = bound.getMinY();
            double max_x = bound.getMaxX();
            double max_y = bound.getMaxY();

            double ori_x = summary.getOrigin().getX();
            double ori_y = summary.getOrigin().getY();
            double des_x = summary.getDestination().getX();
            double des_y = summary.getDestination().getY();

            min_x = Math.min(min_x, Math.min(ori_x, des_x));
            min_y = Math.min(min_y, Math.min(ori_y, des_y));
            max_x = Math.max(max_x, Math.max(ori_x, des_x));
            max_y = Math.max(max_y, Math.max(ori_y, des_y));

            for (int i = 0; i < summary.getWaypoints().length; i++) {
                double waypoint_x = summary.getWaypoints()[i].getX();
                double waypoint_y = summary.getWaypoints()[i].getY();

                min_x = Math.min(min_x, waypoint_x);
                min_y = Math.min(min_y, waypoint_y);
                max_x = Math.max(max_x, waypoint_x);
                max_y = Math.max(max_y, waypoint_y);
            }

            Bound boundEntity = new Bound(min_x, min_y, max_x, max_y,route);

            // RouteRepository를 사용하여 Route 저장
            routeRepository.save(route);
            boundRepository.save(boundEntity);

            // Road 객체 생성 및 설정
            KakaoRouteAllResponseDto.Section[] sections = routeInfo.getSections();

            List<Road> roadList = new ArrayList<>();
            List<Guide> guideList = new ArrayList<>();


            // for문 돌면서 모든 Road의 vertexes를 vertexString에 띄어쓰기로 구분해서 넣어라
            for (KakaoRouteAllResponseDto.Section allSection : sections) {
                KakaoRouteAllResponseDto.Road[] roads = allSection.getRoads();
                KakaoRouteAllResponseDto.Guide[] guides = allSection.getGuides();
                for (KakaoRouteAllResponseDto.Road roadDto : roads) {
                    String vertexesString = "";
                    vertexesString += Arrays.stream(roadDto.getVertexes()).mapToObj(String::valueOf).collect(Collectors.joining(" "));
                    Road roadEntity = new Road(vertexesString, route);
                    roadList.add(roadEntity);
                }
                for (KakaoRouteAllResponseDto.Guide guideDto : guides) {
                    String name = guideDto.getName();
                    double x = guideDto.getX();
                    double y = guideDto.getY();
                    int distance = guideDto.getDistance();
                    int duration = guideDto.getDuration();
                    int type = guideDto.getType();
                    String guidance = guideDto.getGuidance();
                    int roadIndex = guideDto.getRoadIndex();
                    Guide guideEntity = new Guide(name, x, y, distance, duration, type, guidance, roadIndex, route);
                    guideList.add(guideEntity);
                }
            }
//            roadRepository.saveAll(roadList);
            roadJDBCRepository.batchInsert(roadList);

//            guideRepository.saveAll(guideList);
            guideJDBCRepository.batchInsert(guideList);
        }
    }

    public List<AllHistoryResponseDto> getAllHistories(Long userId) {
        List<Route> routes = routeRepository.findAllByUserId(userId);

        return routes.stream()
                .map(route -> new AllHistoryResponseDto(
                        route.getId(),
                        route.getOriginAddress(),
                        route.getDestinationAddress(),
                        route.getMapType(),
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
            throw new IllegalArgumentException("해당 경로를 가져올 수 없습니다.");
        }

        // Bound, Road 정보 가져오기
        Bound bound = route.getBounds().get(0); // 예시로 첫 번째 Bound 가져옴
        List<Road> roads = route.getRoads();
        List<Guide> guides = route.getGuides();

        // HistoryResponseDto 객체 생성 및 설정
        HistoryResponseDto.Bound boundDto = new HistoryResponseDto.Bound(bound.getMinX(), bound.getMinY(), bound.getMaxX(), bound.getMaxY());

        HistoryResponseDto responseDto = new HistoryResponseDto();
        responseDto.setOriginAddress(route.getOriginAddress());
        responseDto.setDestinationAddress(route.getDestinationAddress());
        responseDto.setDuration(route.getDuration());
        responseDto.setDistance(route.getDistance());
        responseDto.setCreatedAt(route.getCreatedAt());
        responseDto.setBounds(boundDto);

        for (Road road : roads) {
            double[] vertices = convertStringToList(road.getVertexes());
            responseDto.getRoads().add(new HistoryResponseDto.Road(vertices));
        }

        for (Guide guide : guides) {
            String name = guide.getName();
            double x = guide.getX();
            double y = guide.getY();
            int distance = guide.getDistance();
            int duration = guide.getDuration();
            int type = guide.getType();
            String guidance = guide.getGuidance();
            int roadIndex = guide.getRoadIndex();
            responseDto.getGuides().add(new HistoryResponseDto.Guide(name, x, y, distance, duration, type, guidance, roadIndex));
        }

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
