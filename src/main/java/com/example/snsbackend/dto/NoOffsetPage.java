package com.example.snsbackend.dto;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
public class NoOffsetPage<T> {
    private List<T> data;   // 현재 페이지 데이터
    private String lastId;  // 다음 페이지 조회를 위한 커서 (마지막 요소 Id
    private int size;
}
