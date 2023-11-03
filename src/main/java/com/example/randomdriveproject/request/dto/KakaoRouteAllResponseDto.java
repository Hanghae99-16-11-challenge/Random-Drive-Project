package com.example.randomdriveproject.request.dto;

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

    public int getWaypointsLength() {
        if (routes[0] != null && routes[0].getSummary() != null && routes[0].getSummary().getWaypoints() != null) {
            return routes[0].getSummary().getWaypoints().length;
        }
        System.out.println("경로 못찾음");
        return 0;
    }

    @Getter
    public static class RouteInfo {
        @JsonProperty("result_code")
        private int resultCode;

        @JsonProperty("result_msg")
        private String resultMsg;

        @JsonProperty("summary")
        private Summary summary;

        @JsonProperty("sections")
        private Section[] sections;

        public Summary getSummary() {
            return summary;
        }

        public Section[] getSections() {
            return sections;
        }

        // Getter, Setter, toString 등 필요한 메서드들은 생략
    }

    @Getter
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

        public Waypoint[] getWaypoints() {
            return waypoints;
        }
        public int getDistance() {
            return distance;
        }

        public int getDuration() {
            return duration;
        }

        public Location getOrigin() {
            return origin;
        }

        public Location getDestination() {
            return destination;
        }

        // Getter, Setter, toString 등 필요한 메서드들은 생략
    }

    @Getter
    public static class Location {
        @JsonProperty("name")
        private String name;

        @JsonProperty("x")
        private double x;

        @JsonProperty("y")
        private double y;

        public String getName() {
            return name;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

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

        public double getMinX() {
            return minX;
        }

        public double getMinY() {
            return minY;
        }

        public double getMaxX() {
            return maxX;
        }

        public double getMaxY() {
            return maxY;
        }

        // Getter, Setter, toString 등 필요한 메서드들은 생략
    }

    public static class Fare {
        @JsonProperty("taxi")
        private int taxi;

        @JsonProperty("toll")
        private int toll;

        // Getter, Setter, toString 등 필요한 메서드들은 생략
    }

    @Getter
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

        public BoundingBox getBound() {
            return bound;
        }

        public Road[] getRoads() {
            return roads;
        }

        public Guide[] getGuides() {
            return guides;
        }

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

        public double[] getVertexes() {
            return vertexes;
        }

        // Getter, Setter, toString 등 필요한 메서드들은 생략
    }

    public static class Waypoint {
        @JsonProperty("name")
        private String name;

        @JsonProperty("x")
        private double x;

        @JsonProperty("y")
        private double y;

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

    }

    @Getter
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

        public String getName() {
            return name;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        public int getDistance() {
            return distance;
        }

        public int getDuration() {
            return duration;
        }

        public int getType() {
            return type;
        }

        public String getGuidance() {
            return guidance;
        }

        public int getRoadIndex() {
            return roadIndex;
        }

        // Getter, Setter, toString 등 필요한 메서드들은 생략
    }

}

