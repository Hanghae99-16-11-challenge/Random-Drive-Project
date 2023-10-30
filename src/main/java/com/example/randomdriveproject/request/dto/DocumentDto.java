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
public class DocumentDto {

    @JsonProperty("place_name") // 카테고리
    private String placeName;

    @JsonProperty("distance") // 카테고리
    private double distance;

    @JsonProperty("category_name") // 카테고리
    private String categoryName;

    @JsonProperty("address_name")
    private String addressName;

    @JsonProperty("y") // y좌표값
    private double latitude;

    @JsonProperty("x") // x좌표값
    private double longitude;

    public DocumentDto(String addressName, double latitude, double longitude) {
        this.addressName = addressName;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String toString() {
        return "DocumentDto{" +
                "placeName='" + placeName + '\'' +
                ", distance=" + distance +
                ", addressName='" + addressName + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", categoryName=" + categoryName +
                '}';
    }
}