package com.example.randomdriveproject.randomNavi.api.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Slf4j
@Service
// 카카오 요청 uri 만드는 곳
public class KakaoUriBuilderService {

    // 주소검색 api
    private static final String KAKAO_LOCAL_SEARCH_ADDRESS_URL = "https://dapi.kakao.com/v2/local/search/address.json";

    // 카테고리 api
    private static final String KAKAO_LOCAL_CATEGORY_SEARCH_URL = "https://dapi.kakao.com/v2/local/search/category.json";
    

    public URI buildUriByAddressSearch(String address) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(KAKAO_LOCAL_SEARCH_ADDRESS_URL);
        uriBuilder.queryParam("query", address);

        URI uri = uriBuilder.build().encode().toUri(); // encoding -> 브라우저에서 해석할 수 없는 것들-> UTF-8 로 인코딩해줌
        log.info("*** 로그 [KakaoUriBuilderService buildUriByAddressSearch] address: {}, uri: {}", address, uri);

        return uri;
    }

    // 목표한 약국에 맞는 URI 구성 -> 카테고리 요청에 맞는 URI
    // 고객이 입력한 주소 -> 위도,경도 기반으로 변환 시킨 것 -> 다시 카테고리 기반으로 인자값
    public URI buildUriByCategorySearch(double y, double x, double radius, String category){

        double meterRadius = radius * 1000;

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(KAKAO_LOCAL_CATEGORY_SEARCH_URL);
        uriBuilder.queryParam("category_group_code", category);
        uriBuilder.queryParam("x", x);
        uriBuilder.queryParam("y", y);
        uriBuilder.queryParam("radius", meterRadius); // 반경 순
        uriBuilder.queryParam("sort", "popularity"); // 인기도 순 정렬

        URI uri = uriBuilder.build().encode().toUri();

        log.info("[KakaoAddressSearchService buildUriByCategorySearch] uri : {} ", uri);

        return uri;
    }

}
