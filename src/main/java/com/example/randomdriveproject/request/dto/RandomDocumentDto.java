package com.example.randomdriveproject.request.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RandomDocumentDto {

//    @JsonProperty("place_name") // 카테고리
//    private String placeName;
//
//    @JsonProperty("distance") // 카테고리
//    private double distance;

    @JsonProperty("address_name")
    private String name;

    @JsonProperty("x") // x좌표값
    private double x;

    @JsonProperty("y") // y좌표값
    private double y;

    public RandomDocumentDto(double x, double y) {
        this.y = y;
        this.x = x;
    }
    public RandomDocumentDto(DocumentDto documentDto) {
        this.name = documentDto.getAddressName();
        this.y = documentDto.getLatitude();
        this.x = documentDto.getLongitude();
    }
}
