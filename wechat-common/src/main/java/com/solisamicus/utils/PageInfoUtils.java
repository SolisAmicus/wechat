package com.solisamicus.utils;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.solisamicus.result.PagedGridResult;

import java.util.List;

public class PageInfoUtils {
    public static PagedGridResult setterPagedGridPlus(Page<?> pageInfo) {
        List<?> list = pageInfo.getRecords();
        PagedGridResult pagedGridResult = new PagedGridResult();
        pagedGridResult.setRows(list);
        pagedGridResult.setPage(pageInfo.getCurrent());
        pagedGridResult.setRecords(pageInfo.getTotal());
        pagedGridResult.setTotal(pageInfo.getPages());
        return pagedGridResult;
    }
}
