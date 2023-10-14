package com.example.randomdriveproject.randomNavi.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RandomMetaDto {

    @JsonProperty("total_count") // 개발 가이드의 Name ->total_count 를 자바의필드와 매칭
    private Integer totalCount;

}