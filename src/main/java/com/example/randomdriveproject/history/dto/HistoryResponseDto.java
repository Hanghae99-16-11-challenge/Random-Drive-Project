package com.example.randomdriveproject.history.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class HistoryResponseDto {

    @JsonProperty("originName")
    private String originName;

    @JsonProperty("destinationName")
    private String destinationName;

    @JsonProperty("duration")
    private int duration;

    @JsonProperty("distance")
    private int distance;

    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

    @JsonProperty("bounds")
    private Bound[] bounds;

    @JsonProperty("roads")
    private Road[] roads;

    public static class Bound {
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

    public static class Road {
        @JsonProperty("vertexes")
        private double[] vertexes;

        // Getter, Setter, toString 등 필요한 메서드들은 생략
    }

}
