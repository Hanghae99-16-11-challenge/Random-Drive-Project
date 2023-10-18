package com.example.randomdriveproject.history.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HistoryResponseDto {

    @JsonProperty("originAddress")
    private String originAddress;

    @JsonProperty("destinationAddress")
    private String destinationAddress;

    @JsonProperty("duration")
    private int duration;

    @JsonProperty("distance")
    private int distance;

    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

    @JsonProperty("bounds")
    private Bound bounds;

    @JsonProperty("roads")
    private List<Road> roads = new ArrayList<>();

    public static class Bound {
        @JsonProperty("min_x")
        private double minX;

        @JsonProperty("min_y")
        private double minY;

        @JsonProperty("max_x")
        private double maxX;

        @JsonProperty("max_y")
        private double maxY;

        public Bound(double minX, double minY, double maxX, double maxY) {
            this.minX = minX;
            this.minY = minY;
            this.maxX = maxX;
            this.maxY = maxY;
        }

        // Getter, Setter, toString 등 필요한 메서드들은 생략
    }

    public static class Road {
        @JsonProperty("vertexes")
        private double[] vertexes;

        public Road(double[] vertexes) {
            this.vertexes = vertexes;
        }

        // Getter, Setter, toString 등 필요한 메서드들은 생략
    }

}
