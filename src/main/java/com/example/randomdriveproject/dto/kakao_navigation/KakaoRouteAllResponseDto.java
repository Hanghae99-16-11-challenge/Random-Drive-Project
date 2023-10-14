package com.example.randomdriveproject.dto.kakao_navigation;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
// 자동차 길찾기api -> 경로에 관한 모든 응답
public class KakaoRouteAllResponseDto {

    @JsonProperty("trans_id")
    private String transId;

    @JsonProperty("routes")
    private RouteInfo[] routes;

    public static class RouteInfo {
        @JsonProperty("result_code")
        private int resultCode;

        @JsonProperty("result_msg")
        private String resultMsg;

        @JsonProperty("summary")
        private Summary summary;

        @JsonProperty("sections")
        private Section[] sections;

        // Getter, Setter, toString 등 필요한 메서드들은 생략
    }

    public static class Summary {
        @JsonProperty("origin")
        private Location origin;

        @JsonProperty("destination")
        private Location destination;

        @JsonProperty("waypoints")
        private Waypoint[] waypoints;

        @JsonProperty("priority")
        private String priority;

        @JsonProperty("bound")
        private BoundingBox bound;

        @JsonProperty("fare")
        private Fare fare;

        @JsonProperty("distance")
        private int distance;

        @JsonProperty("duration")
        private int duration;

        // Getter, Setter, toString 등 필요한 메서드들은 생략
    }

    public static class Location {
        @JsonProperty("name")
        private String name;

        @JsonProperty("x")
        private double x;

        @JsonProperty("y")
        private double y;

        // Getter, Setter, toString 등 필요한 메서드들은 생략
    }

    public static class BoundingBox {
        @JsonProperty("min_x")
        private double minX;

        @JsonProperty("min_y")
        private double minY;

        @JsonProperty("max_x")
        private double maxX;

        @JsonProperty("max_y")
        private double maxY;

        // Getter, Setter, toString 등 필요한 메서드들은 생략
    }

    public static class Fare {
        @JsonProperty("taxi")
        private int taxi;

        @JsonProperty("toll")
        private int toll;

        // Getter, Setter, toString 등 필요한 메서드들은 생략
    }

    public static class Section {
        @JsonProperty("distance")
        private int distance;

        @JsonProperty("duration")
        private int duration;

        @JsonProperty("bound")
        private BoundingBox bound;

        @JsonProperty("roads")
        private Road[] roads;

        @JsonProperty("guides")
        private Guide[] guides;

        // Getter, Setter, toString 등 필요한 메서드들은 생략
    }

    public static class Road {
        @JsonProperty("name")
        private String name;

        @JsonProperty("distance")
        private int distance;

        @JsonProperty("duration")
        private int duration;

        @JsonProperty("traffic_speed")
        private double trafficSpeed;

        @JsonProperty("traffic_state")
        private int trafficState;

        @JsonProperty("vertexes")
        private double[] vertexes;

        // Getter, Setter, toString 등 필요한 메서드들은 생략
    }

    public static class Waypoint {
        @JsonProperty("name")
        private String name;

        @JsonProperty("x")
        private double x;

        @JsonProperty("y")
        private double y;

        // Getter, Setter, toString 등 필요한 메서드들은 생략
    }

    public static class Guide {
        @JsonProperty("name")
        private String name;

        @JsonProperty("x")
        private double x;

        @JsonProperty("y")
        private double y;

        @JsonProperty("distance")
        private int distance;

        @JsonProperty("duration")
        private int duration;

        @JsonProperty("type")
        private int type;

        @JsonProperty("guidance")
        private String guidance;

        @JsonProperty("road_index")
        private int roadIndex;

        // Getter, Setter, toString 등 필요한 메서드들은 생략
    }

}

