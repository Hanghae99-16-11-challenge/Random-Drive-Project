package com.example.randomdriveproject.kakaoapi.service;

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


    public URI buildUriByAddressSearch(String address) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(KAKAO_LOCAL_SEARCH_ADDRESS_URL);
        uriBuilder.queryParam("query", address);

        URI uri = uriBuilder.build().encode().toUri(); // encoding -> 브라우저에서 해석할 수 없는 것들-> UTF-8 로 인코딩해줌
        log.info("*** 로그 [KakaoUriBuilderService buildUriByAddressSearch] address: {}, uri: {}", address, uri);

        return uri;
    }

    // 길찾기
    private static final String KAKAO_ROUTE_SEARCH_URL = "https://apis-navi.kakaomobility.com/v1/directions";


    public URI buildUriByRouteSearch(String origin, String destination) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(KAKAO_ROUTE_SEARCH_URL);

        uriBuilder.queryParam("origin", origin);
        uriBuilder.queryParam("destination", destination);

        URI routeUri = uriBuilder.build().encode().toUri();

        log.info("*** 로그 [KakoaUriBuilerSerivce buildUrilByRoutreSerach] origin: {}, destination: {}, uri: {}", origin, destination ,routeUri);

        return routeUri;
    }
}