package org.example.onepiece.utils;

import lombok.Data;

import java.util.List;

@Data
public class PageInfo<T> {
    private List<T> records;
    private long total;
    private int current;
    private int size;

}