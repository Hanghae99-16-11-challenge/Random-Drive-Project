package com.example.randomdriveproject.user.dto;

import lombok.Getter;

@Getter
public class StatusResponseDto {

    private String msg;
    private int statuscode;

    public StatusResponseDto(String msg, int statuscode) {
        this.msg = msg;
        this.statuscode = statuscode;
    }
}
