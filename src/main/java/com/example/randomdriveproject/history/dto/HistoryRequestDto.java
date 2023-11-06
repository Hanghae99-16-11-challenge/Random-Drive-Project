package com.example.randomdriveproject.history.dto;

import com.example.randomdriveproject.request.dto.KakaoRouteAllResponseDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema
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
