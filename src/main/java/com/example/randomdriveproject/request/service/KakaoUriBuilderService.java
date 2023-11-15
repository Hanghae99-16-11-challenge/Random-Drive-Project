package com.example.randomdriveproject.request.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Slf4j
@Service
// 카카오 요청 uri 만드는 곳
public class KakaoUriBuilderService {

    private static final String KAKAO_LOCAL_SEARCH_ADDRESS_URL = "https://dapi.kakao.com/v2/local/search/address.json"; // 주소검색 api
    private static final String KAKAO_ROUTE_SEARCH_URL = "https://apis-navi.kakaomobility.com/v1/directions"; // 길찾기
    private static final String KAKAO_LOCAL_CATEGORY_SEARCH_URL = "https://dapi.kakao.com/v2/local/search/category.json"; // 카테고리 api
    private static final String KAKAO_LOCAL_KEYWORD_SEARCH_URL = "https://dapi.kakao.com/v2/local/search/keyword.json"; // 키워드 api


    // 주소검색
    public URI buildUriByAddressSearch(String address) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(KAKAO_LOCAL_SEARCH_ADDRESS_URL);
        uriBuilder.queryParam("query", address);

        URI uri = uriBuilder.build().encode().toUri(); // encoding -> 브라우저에서 해석할 수 없는 것들-> UTF-8 로 인코딩해줌
        log.info("주소 검색 API URI 생성 - 주소: {}, URI: {}", address, uri);

        return uri;
    }


    // 길찾기
    public URI buildUriByRouteSearch(String origin, String destination) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(KAKAO_ROUTE_SEARCH_URL);

        uriBuilder.queryParam("origin", origin);
        uriBuilder.queryParam("destination", destination);

        URI routeUri = uriBuilder.build().encode().toUri();

        log.info("길찾기 API URI 생성 - 출발지: {}, 목적지: {}, URI: {}", origin, destination ,routeUri);

        return routeUri;
    }


    // 카테고리
    public URI buildUriByCategorySearch(double y, double x, double radius, String category){

        double meterRadius = radius * 1000;

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(KAKAO_LOCAL_CATEGORY_SEARCH_URL);
        uriBuilder.queryParam("category_group_code", category);
        uriBuilder.queryParam("x", x);
        uriBuilder.queryParam("y", y);
        uriBuilder.queryParam("radius", meterRadius); // 반경 순
        uriBuilder.queryParam("sort", "popularity");

        URI uri = uriBuilder.build().encode().toUri();

        log.info("카테고리 검색 API URI 생성 - 좌표(y: {}, x: {}), 반경: {}m, 카테고리: {}, URI: {}", y, x, meterRadius, category, uri);

        return uri;
    }


    // 경로 재생성 길찾기
    public URI buildUriByReRouteSearch(double startCoord) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(KAKAO_ROUTE_SEARCH_URL);

//        uriBuilder.queryParam("lat", lat);
//        uriBuilder.queryParam("lng", lng);
//        uriBuilder.queryParam("destination", routeRepository.findBy());
        uriBuilder.queryParam("startCoord", startCoord);

        URI routeUri = uriBuilder.build().encode().toUri();

        log.info("경로 재생성 길찾기 API URI 생성 - 시작 좌표: {}, URI: {}", startCoord, routeUri);

        return routeUri;
    }


    // 키워드
    public URI buildUriByKeywordSearch(String query){

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(KAKAO_LOCAL_KEYWORD_SEARCH_URL);
        uriBuilder.queryParam("query", query);

        URI uri = uriBuilder.build().encode().toUri(); // encode 제거

        log.info("키워드 검색 API URI 생성 - 키워드: {}, URI: {}", query, uri);

        return uri;
    }
}