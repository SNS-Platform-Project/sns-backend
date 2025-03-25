package com.example.snsbackend.dto;

import lombok.Data;
import lombok.Getter;
import org.springframework.lang.Nullable;

import javax.print.attribute.standard.NumberUp;

@Data
@Getter
public class PageParam {
    private String lastId = null;
    private int size = 5;
}
