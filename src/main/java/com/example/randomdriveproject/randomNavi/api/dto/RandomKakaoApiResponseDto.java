package com.example.randomdriveproject.randomNavi.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RandomKakaoApiResponseDto {

    @JsonProperty("meta")
    private RandomMetaDto randomMetaDto;

    @JsonProperty("documents")
    private List<RandomDocumentDto> randomDocumentDtoList;
}