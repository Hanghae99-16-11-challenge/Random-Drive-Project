package com.example.randomdriveproject.history.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AllHistoryResponseDto {
    @JsonProperty("route_id")
    private Long routeId;

    @JsonProperty("originAddress")
    private String originAddress;

    @JsonProperty("destinationAddress")
    private String destinationAddress;

    @JsonProperty("mapType")
    private String mapType;

    @JsonProperty("duration")
    private int duration;

    @JsonProperty("distance")
    private int distance;

    @JsonProperty("createdAt")
    private LocalDateTime createdAt;
}
