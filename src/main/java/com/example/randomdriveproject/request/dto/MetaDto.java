package com.example.randomdriveproject.request.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MetaDto {

    @JsonProperty("total_count")
    private Integer totalCount;

}