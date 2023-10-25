package com.example.randomdriveproject.history.dto;

import com.example.randomdriveproject.request.dto.KakaoRouteAllResponseDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class HistoryRequestDto {
    @JsonProperty("requestData")
    private KakaoRouteAllResponseDto requestData;
    @JsonProperty("originAddress")
    private String originAddress;
    @JsonProperty("destinationAddress")
    private String destinationAddress;
    @JsonProperty("mapType")
    private String mapType;
}
