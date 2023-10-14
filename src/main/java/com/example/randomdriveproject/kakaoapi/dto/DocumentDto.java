package com.example.randomdriveproject.kakaoapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DocumentDto {

//    @JsonProperty("place_name") // 카테고리
//    private String placeName;
//
//    @JsonProperty("distance") // 카테고리
//    private double distance;

    @JsonProperty("address_name")
    private String addressName;

    @JsonProperty("y") // y좌표값
    private double latitude;

    @JsonProperty("x") // x좌표값
    private double longitude;
}