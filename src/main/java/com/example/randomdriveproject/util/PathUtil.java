package com.example.randomdriveproject.util;

import com.example.randomdriveproject.request.dto.KakaoRouteAllResponseDto;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.util.concurrent.TimeUnit;

@Slf4j(topic = "PathUtil")
public class PathUtil {
    public static void PathInfo(KakaoRouteAllResponseDto response , String callBy)
    {
        int duration = response.getRoutes()[0].getSummary().getDuration();
        log.info(callBy + " - 거리 : " + response.getRoutes()[0].getSummary().getDistance()
                + "m / 소요 시간 : " + duration + " -> " + (duration / 60) + "m" + (duration - ((duration/60) * 60)) + "s");



        log.info("경유지 갯수 : " + response.getRoutes()[0].getSummary().getWaypoints().length);

        int guidesCount = 0;
        StringBuilder sb = new StringBuilder();

        for(var sec : response.getRoutes()[0].getSections())
        {
            for(var gui : sec.getGuides())
            {
                guidesCount++;
                sb.append(" / " + gui.getName());
                //gui.getDistance()
                //gui.getDuration()
            }
        }

        log.info("안내 정보 : " + guidesCount + " / " +  sb.toString());
        
        // ++ 현위치 와 출발지의 직선 거리
        
        //소유 시간이 현제 교통상황, 교차로 대기시간 등 여러 부분을 종합한 결과가 아닐까?
        // 그렇다면 안내정보 기반하여 남은 시간 추출하는게 더 정확하지 않을까?

        //추가로 서버리스 개념을 추가해서 , 가장 가까운 안내지점을 클라이언트가 계산하고
        // 길안내를 한다면 현위치 ~ 출발지 ~ 도착지  이렇게 길 찾아야 하지 않을까?
            // 일정 거리 이내 이면 그냥 출발지 ~ 도착지
        //navigator.geolocation.getCurrentPosition 문제 : 아마 노트북이라 고도 , 방향 , 속도 측정 불가 + 오차가 450m
            // 정확도 높혀도 마찬가지
        //

        //  "/all-random-route" -> 일정 반경내에서 인기순으로 정렬된 관광 명소를 랜덤으로 뽑아 목적지와 도착지를 정함
        //
    }

    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        lat1 = Math.toRadians(lat1);
        lon1 = Math.toRadians(lon1);
        lat2 = Math.toRadians(lat2);
        lon2 = Math.toRadians(lon2);

        double earthRadius = 6371; //Kilometers
        return earthRadius * Math.acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));
    }
}
