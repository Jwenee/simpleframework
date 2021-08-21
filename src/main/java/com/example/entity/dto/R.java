package com.example.entity.dto;

import lombok.Data;

@Data
public class R<T> {

    private int code;

    private String msg;

    private T data;
}
