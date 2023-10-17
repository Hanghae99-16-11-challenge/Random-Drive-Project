package com.example.randomdriveproject.user.dto;

import lombok.Getter;

@Getter
public class MessageResponseDto {

    private String msg;
    private Integer statusCode;

    public MessageResponseDto(String msg, Integer statusCode) {
        this.msg = msg;
        this.statusCode = statusCode;
    }
}
