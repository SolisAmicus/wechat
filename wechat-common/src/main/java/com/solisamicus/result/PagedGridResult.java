package com.solisamicus.result;

import lombok.Data;

import java.util.List;

@Data
public class PagedGridResult {
    private long page;
    private long total;
    private long records;
    private List<?> rows;
}
